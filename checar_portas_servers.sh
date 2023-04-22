#!/bin/bash

for i in {25506..25515} ; do lsof -i :"$i" ; done

for i in {60552..60552} ; do lsof -i :"$i" ; done
