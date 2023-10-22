#!/usr/bin/env python

import os
import sys

base_dir = os.path.abspath('./src/main/kotlin')

def build(ls, prefix, base):
    base += '/'
    files = os.listdir(base)
    files.sort()
    has_java = False
    for f in files:
        if f.endswith('.kt'):
            has_java = True
            break
    if prefix != '':
        if has_java:
            ls.append(prefix)
        prefix += '.'
    for f in files:
        if os.path.isdir(base + f):
            build(ls, prefix + f, base + f)

ls = []
build(ls, '', base_dir)
for f in ls:
    print 'exports ' + f + ';'
