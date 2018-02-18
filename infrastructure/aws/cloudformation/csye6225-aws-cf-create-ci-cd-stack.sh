#!/bin/bash

if [ -z "$1" ]
then
	echo "No command line argument provided for stack STACK_NAME"
	exit 1
fi

echo "Validating template"
RC=$(aws cloudformation validate-template --template-body file://./csye6225-cf-ci-cd.json)
echo "Template is valid"

if [ $? -eq 0 ]
then
	echo "Success: validate template"
else
	echo "Fail validate template"
	exit 1
fi

# Domain name for ARN
echo "Fetching domain name from Route 53"
DOMAIN_NAME=$(aws route53 list-hosted-zones --query HostedZones[0].Name --output text)
DOMAIN_NAME="s3."${DOMAIN_NAME%?}

#AWS Region
AWS_REGION="us-east-1a"

# Account id for arn
echo "Fetching user's account id"
ACCOUNT_ID=$(aws sts get-caller-identity --query 'Account' --output text)

aws cloudformation create-stack --stack-name $1-CI --template-body file://./csye6225-cf-ci-cd.json --parameters ParameterKey=S3_ARN,ParameterValue=arn:aws:s3:::$DOMAIN_NAME ParameterKey=AWS_REGION,ParameterValue=$AWS_REGION
ParameterKey=ACCOUNT_ID,ParameterValue=$ACCOUNT_ID
