#ifndef COMPRESS_H
#define COMPRESS_H

#include"Huffman.h"
#include<iostream>
struct HEAD {
	char type[4];
	int length;
	int weight[256];
};

int InitHead(const char* pFilename, HEAD& sHead);

int Compress(const char* pFilename);
int Decompress(const char* pFilename);

int Encode(const char* pFilename, const HuffmanCode pHC, char* pBuffer, const int nSize);
int WriteFile(const char* pFilename, const HEAD sHead, const char* pBuffer, const int nSize);

char Str2byte(const char* pBinStr);


#endif