#!/bin/bash

if [ -z "$1" ]
then
	echo "No command line argument provided for stack STACK_NAME"
	exit 1
else
	echo "Started with creating resources using cloud formation"
fi


RC=$(aws cloudformation describe-stacks)

RC=$(aws cloudformation validate-template --template-body file://./csye6225-cf-networking.json)


if [ $? -eq 0 ]
then
	echo "Success: validate template"
else
	echo "Fail validate template"
	exit 1
fi

RC=$(aws cloudformation create-stack --stack-name $1 --template-body file://./csye6225-cf-networking.json --parameters ParameterKey=VPCNAME,ParameterValue=$1-csye6225-vpc ParameterKey=IGWNAME,ParameterValue=$1-csye6225-InternetGateway ParameterKey=ROUTETABLENAME,ParameterValue=$1-csye6225-public-route-table )

echo "Stack creation in progress. Please wait"
aws cloudformation wait stack-create-complete --stack-name $1
STACKDETAILS=$(aws cloudformation describe-stacks --stack-name $1 --query Stacks[0].StackId --output text)

echo "Stack creation complete"
echo "Stack id: $STACKDETAILS"
exit 0