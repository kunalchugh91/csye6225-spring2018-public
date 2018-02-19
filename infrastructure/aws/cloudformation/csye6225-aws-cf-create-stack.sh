#!/bin/bash

if [ -z "$1" ]
then
	echo "No command line argument provided for stack STACK_NAME"
	exit 1
else
	echo "Stack name present. Checking for CIDRBlock"
fi

if [ -z "$2" ]
then
	echo "No command line argument provided for CIDRBlock"
	exit 1
else
	echo "CIDRBlock address provided."
fi

echo "Validating template"
RC=$(aws cloudformation validate-template --template-body file://./csye6225-cf-networking.json)
echo "Template is valid"

if [ $? -eq 0 ]
then
	echo "Success: validate template"
else
	echo "Fail validate template"
	exit 1
fi

echo "Started with creating networking stack using cloud formation"
RC=$(aws cloudformation create-stack --stack-name $1-networking --template-body file://./csye6225-cf-networking.json --parameters ParameterKey=VPCNAME,ParameterValue=$1-csye6225-vpc ParameterKey=IGWNAME,ParameterValue=$1-csye6225-InternetGateway ParameterKey=PUBLICROUTETABLENAME,ParameterValue=$1-csye6225-public-route-table ParameterKey=PRIVATEROUTETABLENAME,ParameterValue=$1-csye6225-private-route-table  ParameterKey=CIDRBLOCK,ParameterValue=$2)

echo "Networking stack creation in progress. Please wait"
aws cloudformation wait stack-create-complete --stack-name $1-networking
STACKDETAILS=$(aws cloudformation describe-stacks --stack-name $1-networking --query Stacks[0].StackId --output text)
echo "Networking stack creation complete"
echo "Networking Stack id: $STACKDETAILS"

echo "Creating application stack"

echo "Fetching VPC details"
VPC_ID=$(aws ec2 describe-vpcs --query Vpcs[0].VpcId --output text)

echo "Fetching domain name from Route 53"
DOMAIN_NAME=$(aws route53 list-hosted-zones --query HostedZones[0].Name --output text)
DOMAIN_NAME="web-app."${DOMAIN_NAME%?}

SUBNET_ID_1=$(aws cloudformation list-stack-resources --stack-name $1-networking --query 'StackResourceSummaries[?LogicalResourceId==`PrivateSubnet1`][PhysicalResourceId]' --output text)
SUBNET_ID_2=$(aws cloudformation list-stack-resources --stack-name $1-networking --query 'StackResourceSummaries[?LogicalResourceId==`PrivateSubnet2`][PhysicalResourceId]' --output text)

aws cloudformation create-stack --stack-name $1-application --template-body file://./csye6225-cf-application.json --parameters ParameterKey=SUBNETID1,ParameterValue=$SUBNET_ID_1 ParameterKey=SUBNETID2,ParameterValue=$SUBNET_ID_2 ParameterKey=DOMAIN,ParameterValue=$DOMAIN_NAME

aws cloudformation wait stack-create-complete --stack-name $1-application
STACKDETAILS=$(aws cloudformation describe-stacks --stack-name $1-application --query Stacks[0].StackId --output text)
echo "Application stack creation complete"
echo "Application Stack id: $STACKDETAILS"

exit 0
