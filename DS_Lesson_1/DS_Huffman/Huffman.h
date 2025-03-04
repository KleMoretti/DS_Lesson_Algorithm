#pragma once
struct HTNode
{
    int weight;
    int parent;
    int lchild;
    int rchild;
};

typedef HTNode* HuffmanTree;
typedef char** HuffmanCode;


void CreateHuffmanTree(HuffmanTree& pHT, int* weight, int n);
void HuffmanCoding(HuffmanCode& pHC, HuffmanTree& pHT);
int Select(HuffmanTree pHT, int nSize);

void TestHuffTree(HuffmanTree pHT);
void TestHuffTreeN(int root, HuffmanTree pHT);
void TestHuffCode(int root, HuffmanTree pHT, HuffmanCode pHC);