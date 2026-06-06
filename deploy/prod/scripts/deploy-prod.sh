#!/usr/bin/env sh
set -eu

SCRIPT_DIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
PROD_DIR=$(CDPATH= cd -- "$SCRIPT_DIR/.." && pwd)
DEPLOY_ROOT=${DEPLOY_ROOT:-$(CDPATH= cd -- "$PROD_DIR/../.." && pwd)}

ENV_FILE="$PROD_DIR/env/prod.env"
ENV_EXAMPLE_FILE="$PROD_DIR/env/prod.env.example"
FRONTEND_DIST="$DEPLOY_ROOT/artifacts/frontend/dist"
NGINX_HTML="$PROD_DIR/conf/nginx/html"

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
docker compose --env-file "$ENV_FILE" -f compose/compose.prod.yml up -d --build postgres redis gateway nginx
