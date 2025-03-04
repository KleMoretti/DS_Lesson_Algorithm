#include "Huffman.h"
#include <cstdio>
#include <cstdlib>
#include <iostream>

int Select(HuffmanTree pHT, int nSize) {
    int minValue = 0x7FFFFFFF;
    int min = 0;
    for (int i = 1; i <= nSize; i++) {
        if (pHT[i].parent == 0 && pHT[i].weight < minValue) {
            minValue = pHT[i].weight;
            min = i;
        }
    }
    return min;
}



void CreateHuffmanTree(HuffmanTree& pHT, int* weight, int n) {
    if (n <= 1) return;
    int m = 2 * n - 1;

    for (int i = 1; i <= n; ++i) {
        pHT[i].weight = weight[i - 1]; // weight ����� 0 ��ʼ
        pHT[i].parent = 0;
        pHT[i].lchild = 0; // ��Ϊ 0
        pHT[i].rchild = 0; // ��Ϊ 0
    }
    for (int i = n + 1; i <= m; ++i) {
        pHT[i].weight = 0;
        pHT[i].parent = 0;
        pHT[i].lchild = 0; // ��Ϊ 0
        pHT[i].rchild = 0; // ��Ϊ 0
    }
    for (int i = n + 1; i <= m; ++i) {
        int s1 = Select(pHT, i - 1);
        pHT[s1].parent = i;
        int s2 = Select(pHT, i - 1);
        pHT[s2].parent = i;
        pHT[i].lchild = s1;
        pHT[i].rchild = s2;
        pHT[i].weight = pHT[s1].weight + pHT[s2].weight;
    }
}


void HuffmanCoding(HuffmanCode& pHC, HuffmanTree& pHT) {
    // ���ȳ�ʼ������ָ��ΪNULL
    for (int i = 0; i < 256; i++) {
        pHC[i] = NULL;
    }

    char cd[256] = { '\0' };
    int cdlen = 0;
    for (int i = 1; i < 512; i++) {
        pHT[i].weight = 0;
    }
    int p = 511;
    while (p != 0) {
        if (pHT[p].weight == 0) {
            pHT[p].weight = 1;
            if (pHT[p].lchild != 0) {
                p = pHT[p].lchild;
                cd[cdlen++] = '0';
            }
            else if (pHT[p].rchild == 0) {
                // ����һ���ַ��ڵ㣬���䲢�������
                int index = p - 1;
                if (index >= 0 && index < 256) {
                    pHC[index] = (char*)malloc((cdlen + 1) * sizeof(char));
                    if (pHC[index]) {  // ����ڴ�����Ƿ�ɹ�
                        cd[cdlen] = '\0';
                        strcpy(pHC[index], cd);
                    }
                }
            }
        }
        else if (pHT[p].weight == 1) {
            pHT[p].weight = 2;
            // �Һ���ΪҶ�ӽڵ�
            if (pHT[p].rchild != 0) {
                p = pHT[p].rchild;
                cd[cdlen++] = '1';
            }
        }
        // �˻ظ��ڵ㣬���볤�ȼ�һ
        else {
            pHT[p].weight = 0;
            p = pHT[p].parent;
            if (cdlen > 0) cdlen--;  // ��������
        }
    }
}

void TestHuffTree(HuffmanTree pHT) {
    for (int i = 1; i < 512; i++) {
        printf("pHT[%d]\t%d\t%d\t%d\t%d\n", i,
            pHT[i].weight, pHT[i].parent, pHT[i].lchild, pHT[i].rchild);
    }
}

void TestHuffCode(int root, HuffmanTree pHT, HuffmanCode pHC) {
    if (pHT[root].lchild == 0 && pHT[root].rchild == 0) {
        printf("0x%02X %s\n", root - 1, pHC[root - 1]);
    }
    if (pHT[root].lchild) {
        TestHuffCode(pHT[root].lchild, pHT, pHC);
    }
    if (pHT[root].rchild) {
        TestHuffCode(pHT[root].rchild, pHT, pHC);
    }
}

void TestHuffTreeN(int root, HuffmanTree pHT) {
    std::cout << pHT[root].weight << " ";
    if (pHT[root].lchild != 0) {
        TestHuffTreeN(pHT[root].lchild, pHT);
    }
    if (pHT[root].rchild != 0) {
        TestHuffTreeN(pHT[root].rchild, pHT);
    }
}