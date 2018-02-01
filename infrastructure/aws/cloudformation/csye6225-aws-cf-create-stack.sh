#!/bin/bash

if [ -z "$1" ]
then
	echo "No command line argument provided for stack STACK_NAME"
	exit 1
else
	echo "Started with creating resources using cloud formation"
fi

RC=$(aws cloudFormation describe-stacks)

RC=$(aws cloudformation validate-template --template-body file:./csye6225-cf-networking.json)

if [ $? -eq 0 ]
then
	echo "Success: validate template"
else
	echo "Fail validate template"
	exit 1
fi

RC=$(aws cloudFormation create-stack --stack-name $1 --template-body file:./csye6225-cf-networking.json --parameters ParameterKey=STACK_NAME,ParameterValue=$1)

if [ $? -eq 0 ]
then
	echo "Success: Stack created"
else
	echo "Fail Stack created"
	exit 1
fi

RC=$(aws cloudformation list-stacks)
RC=$(aws cloudformation describe-stack-events --stack-name $1)
RC=$(aws cloudformation describe-stacks --stack-name $1)

RC=$(aws cloudformation delete-stack --stack-name $1)

if [ $? -eq 0 ]
then
	echo "Success: Stack deleted"
else
	echo "Fail Stack deleted"
	exit 1
fi



