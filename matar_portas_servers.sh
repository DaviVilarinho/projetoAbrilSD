#!/bin/bash

for i in {25506..25515} ; do fuser --kill "$i"/tcp ; done

for i in {60552..60557} ; do fuser --kill "$i"/tcp ; done
