#!/bin/bash

set -e

BUCKET_NAME=book-review

echo "Create bucket"

awslocal s3 mb s3://${BUCKET_NAME}

echo "Upload images"

awslocal s3 cp \
  /images/66732f30-4952-4c2b-90a7-401ede7076bc.png \
  s3://${BUCKET_NAME}/66732f30-4952-4c2b-90a7-401ede7076bc.png

awslocal s3 cp \
  /images/3bf5b4e7-8aac-4bae-8dbc-0b6ac766d091.png \
  s3://${BUCKET_NAME}/3bf5b4e7-8aac-4bae-8dbc-0b6ac766d091.png

awslocal s3 cp \
  /images/342c2891-fcb2-4c5e-aeaa-e8bd41bf3e17.png \
  s3://${BUCKET_NAME}/342c2891-fcb2-4c5e-aeaa-e8bd41bf3e17.png

echo "Completed"