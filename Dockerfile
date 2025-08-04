FROM ubuntu:latest
LABEL authors="dnyan"

ENTRYPOINT ["top", "-b"]