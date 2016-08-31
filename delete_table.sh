#!/usr/bin/env bash

export AWS_ACCESS_KEY_ID="dummy"
export AWS_SECRET_ACCESS_KEY="dummy"

aws dynamodb --endpoint-url "http://localhost:8000" delete-table --table-name Music
