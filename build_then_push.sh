# 分别构建
docker buildx build --platform linux/amd64 -t opoojkk/local-board-server:tag-amd64 --push .
docker buildx build --platform linux/arm64 -t opoojkk/local-board-server:tag-arm64 --push .
docker buildx build --platform linux/arm/v7 -t opoojkk/local-board-server:tag-armv7 --push .

# 然后创建manifest
docker manifest create opoojkk/local-board-server:tag \
    opoojkk/local-board-server:tag-amd64 \
    opoojkk/local-board-server:tag-arm64 \
    opoojkk/local-board-server:tag-armv7
docker manifest push opoojkk/local-board-server:tag

