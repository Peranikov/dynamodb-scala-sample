#!/usr/bin/env bash

export AWS_ACCESS_KEY_ID="dummy"
export AWS_SECRET_ACCESS_KEY="dummy"

aws dynamodb --endpoint-url "http://localhost:8000" create-table \
    --table-name Music \
    --attribute-definitions \
        AttributeName=Artist,AttributeType=S \
        AttributeName=SongTitle,AttributeType=S \
        AttributeName=Origin,AttributeType=S \
    --key-schema \
        AttributeName=Artist,KeyType=HASH \
        AttributeName=SongTitle,KeyType=RANGE \
    --provisioned-throughput \
        ReadCapacityUnits=1,WriteCapacityUnits=1 \
    --local-secondary-indexes "IndexName=OriginIndex,KeySchema=[{AttributeName=Artist,KeyType=HASH},{AttributeName=Origin,KeyType=RANGE}],Projection={ProjectionType=KEYS_ONLY}" \
    --global-secondary-indexes "IndexName=OriginGlobalIndex,KeySchema=[{AttributeName=Origin,KeyType=HASH},{AttributeName=Artist,KeyType=RANGE}],Projection={ProjectionType=KEYS_ONLY},ProvisionedThroughput={ReadCapacityUnits=1,WriteCapacityUnits=1}"
