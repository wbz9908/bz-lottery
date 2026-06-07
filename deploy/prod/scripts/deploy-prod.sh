#!/usr/bin/env sh
set -eu

SCRIPT_DIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
PROD_DIR=$(CDPATH= cd -- "$SCRIPT_DIR/.." && pwd)
DEPLOY_ROOT=${DEPLOY_ROOT:-$(CDPATH= cd -- "$PROD_DIR/../.." && pwd)}

ENV_FILE="$PROD_DIR/env/prod.env"
ENV_EXAMPLE_FILE="$PROD_DIR/env/prod.env.example"
FRONTEND_DIST="$DEPLOY_ROOT/artifacts/frontend/dist"
NGINX_HTML="$PROD_DIR/conf/nginx/html"
IMAGE_ARCHIVE_DIR="$DEPLOY_ROOT/artifacts/images"

load_runtime_images() {
  if [ ! -d "$IMAGE_ARCHIVE_DIR" ]; then
    echo "Runtime image archive directory not found: $IMAGE_ARCHIVE_DIR" >&2
    exit 1
  fi

  loaded_count=0
  for archive in "$IMAGE_ARCHIVE_DIR"/*.tar.gz; do
    if [ ! -f "$archive" ]; then
      continue
    fi

    echo "Loading runtime image from release bundle: $(basename "$archive")"
    docker load -i "$archive"
    loaded_count=$((loaded_count + 1))
  done

  if [ "$loaded_count" -eq 0 ]; then
    echo "No runtime image archives found in $IMAGE_ARCHIVE_DIR" >&2
    exit 1
  fi

  export GATEWAY_IMAGE=lottery-platform/gateway:latest
  export USER_IMAGE=lottery-platform/user:latest
  export POSTGRES_IMAGE_TAG=17-alpine
  export REDIS_IMAGE_TAG=7-alpine
}

if [ ! -f "$ENV_FILE" ]; then
  cp "$ENV_EXAMPLE_FILE" "$ENV_FILE"
  echo "Created $ENV_FILE from prod.env.example. Review secrets before exposing the site publicly."
fi

if [ -d "$FRONTEND_DIST" ]; then
  rm -rf "$NGINX_HTML"
  mkdir -p "$NGINX_HTML"
  cp -R "$FRONTEND_DIST"/. "$NGINX_HTML"/
else
  echo "Frontend dist not found: $FRONTEND_DIST" >&2
  exit 1
fi

mkdir -p "$PROD_DIR/data/nginx/logs" "$PROD_DIR/data/postgres" "$PROD_DIR/data/redis"

cd "$PROD_DIR"
load_runtime_images
docker compose --env-file "$ENV_FILE" -f compose/compose.prod.yml up -d --no-build postgres redis
docker compose --env-file "$ENV_FILE" -f compose/compose.prod.yml up -d --no-build --force-recreate user gateway nginx
