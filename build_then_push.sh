docker buildx build --platform linux/amd64,linux/arm64,linux/arm/v7 \ 
  -t opoojkk/local-board-server:0.0.2 \
  --push .