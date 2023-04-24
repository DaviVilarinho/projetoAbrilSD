#!/bin/bash

for i in {25506..25509} ; do fuser --kill "$i"/tcp || echo "nada a matar em $i"; done

for i in {60552..60555} ; do fuser --kill "$i"/tcp || echo "nada a matar em $i"; done
