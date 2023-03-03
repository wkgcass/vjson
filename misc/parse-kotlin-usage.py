#!/usr/bin/env python3

import sys

def valid(c, prev):
	if prev == ',' and c != ' ':
		return False
	return  ('a' <= c and c <= 'z') or \
		('A' <= c and c <= 'Z') or \
		('0' <= c and c <= '9') or \
		('$' == c or '_' == c) or \
		('/' == c or '.' == c or ':' == c) or \
		('"' == c or '<' == c or '>' == c) or \
		(';' == c or '(' == c or ')' == c) or \
		('*' == c) or \
		(',' == c or (' ' == c and ',' == prev) or '[' == c) or \
		('-' == c) or \
		False

def handle(line):
	idx = 0
	while True:
		idx = line.find('kotlin', idx)
		if idx == -1:
			break
		s = ''
		prev = None
		while idx < len(line):
			c = line[idx]
			if valid(c, prev):
				idx += 1
				s += c
				prev = c
			else:
				break
		print (s)
		if idx >= len(line):
			break

def main():
	while True:
		line = sys.stdin.readline()
		if line is None or line == '':
			return 0
		handle(line)

sys.exit(main())
