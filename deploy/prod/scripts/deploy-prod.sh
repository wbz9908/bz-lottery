#!/usr/bin/env sh
set -eu

SCRIPT_DIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
PROD_DIR=$(CDPATH= cd -- "$SCRIPT_DIR/.." && pwd)
DEPLOY_ROOT=${DEPLOY_ROOT:-$(CDPATH= cd -- "$PROD_DIR/../.." && pwd)}

ENV_FILE="$PROD_DIR/env/prod.env"
ENV_EXAMPLE_FILE="$PROD_DIR/env/prod.env.example"
FRONTEND_DIST="$DEPLOY_ROOT/artifacts/frontend/dist"
NGINX_HTML="$PROD_DIR/conf/nginx/html"
REDIS_IMAGE_ARCHIVE="$DEPLOY_ROOT/artifacts/images/redis-7-alpine.tar.gz"

read_env_value() {
  key="$1"
  file="$2"
  if [ -f "$file" ]; then
    sed -n "s/^${key}=//p" "$file" | tail -n 1
  fi
}

ensure_redis_image() {
  if [ -f "$REDIS_IMAGE_ARCHIVE" ]; then
    echo "Loading redis:7-alpine from release bundle."
    docker load -i "$REDIS_IMAGE_ARCHIVE"
    export REDIS_IMAGE_TAG=7-alpine
    return 0
  fi

  current_tag=$(read_env_value REDIS_IMAGE_TAG "$ENV_FILE")
  current_tag=${current_tag:-7-alpine}

  if docker pull "redis:${current_tag}"; then
    return 0
  fi

  if [ "$current_tag" = "7-alpine" ]; then
    return 1
  fi

  echo "redis:${current_tag} is unavailable from the current registry mirror. Falling back to redis:7-alpine."
  export REDIS_IMAGE_TAG=7-alpine
  docker pull redis:7-alpine
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
ensure_redis_image
docker compose --env-file "$ENV_FILE" -f compose/compose.prod.yml up -d --build postgres redis gateway nginx
