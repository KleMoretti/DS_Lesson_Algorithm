#include "Compress.h"
#include"Huffman.h"
#include<iostream>
#include <cstdio>
#include <bitset>

using namespace std;

int InitHead(const char* pFilename, HEAD& sHead)
{
    strcpy(sHead.type, "HUF");
    sHead.length = 0;
    for (int i = 0; i < 256; i++) {
        sHead.weight[i] = 0;
    }
    FILE* in = fopen(pFilename, "rb");
    int ch;
    while ((ch = fgetc(in)) != EOF) {
        sHead.weight[ch]++;
        sHead.length++;
    }

    fclose(in);
    in = NULL;

    return 1;
}
int Compress(const char* pFilename)
{
    cout << "Reading file...." << endl;
    int weight[256] = { 0 };
    FILE* in = fopen(pFilename, "rb");

    int ch;
    while ((ch = fgetc(in)) != EOF) {
        weight[ch]++;
    }
    cout << "Finish reading file...\n" << endl;
    fclose(in);

    HTNode* pHT = (HTNode*)malloc(512 * sizeof(HTNode));
    if (!pHT) {
        cerr << "Memory allocation failed for Huffman tree" << endl;
        return 0;
    }
    memset(pHT, 0, 512 * sizeof(HTNode));

    HuffmanCode pHC = (HuffmanCode)malloc(256 * sizeof(char*));
    // ��ʼ��Huffman����ָ��
    for (int i = 0; i < 256; i++) {
        pHC[i] = NULL;
    }

    CreateHuffmanTree(pHT, weight, 256);

    HuffmanCoding(pHC, pHT);

    // ����ѹ����Ĵ�С
    unsigned long long bitCount = 0;
    for (int i = 0; i < 256; i++) {
        if (weight[i] > 0 && pHC[i] != NULL) {
            bitCount += (unsigned long long)weight[i] * strlen(pHC[i]);
        }
    }

    int byteCount = (bitCount + 7) / 8;  // ����ȡ�����ֽ�
    cout << "Estimated compressed data size: " << byteCount << " bytes" << endl;

    
    // ���仺����
    char* pBuffer = (char*)malloc(byteCount);
    if (!pBuffer) {
        cerr << "Memory allocation failed for compression buffer" << endl;
        for (int i = 0; i < 256; i++) {
            if (pHC[i]) free(pHC[i]);
        }
        free(pHC);
        free(pHT);
        return 0;
    }
    memset(pBuffer, 0, byteCount);

    // �����ļ�
    if (!Encode(pFilename, pHC, pBuffer, byteCount)) {
        cerr << "Encoding failed" << endl;
        free(pBuffer);
        for (int i = 0; i < 256; i++) {
            if (pHC[i]) free(pHC[i]);
        }
        free(pHC);
        free(pHT);
        return 0;
    }

    // ����ͷ��Ϣ��д���ļ�
    HEAD sHead;
    InitHead(pFilename, sHead);

    int compressedSize = WriteFile(pFilename, sHead, pBuffer, byteCount);

    // ����ѹ����
    double ratio = (double)compressedSize * 100.0 / sHead.length;
    cout << "Original size: " << sHead.length << " bytes" << endl;
    cout << "Compressed size: " << compressedSize << " bytes" << endl;
    cout << "Compression ratio: " << ratio << "%" << endl;

    // �ͷ��ڴ�
    free(pBuffer);
    for (int i = 0; i < 256; i++) {
        if (pHC[i]) free(pHC[i]);
    }
    free(pHC);
    free(pHT);

    return 1;


}


int Decompress(const char* pFilename) {
    HEAD sHead;
    FILE* in = fopen(pFilename, "rb");
    if (!in) {
        cerr << "Failed to open file." << endl;
        return 0;
    }

    // ��ȡ�ļ�ͷ
    if (fread(&sHead, sizeof(HEAD), 1, in) != 1) {
        cerr << "Invalid header format." << endl;
        fclose(in);
        return 0;
    }

    if (strcmp(sHead.type, "HUF") != 0) {
        cerr << "Not a valid Huffman compressed file." << endl;
        fclose(in);
        return 0;
    }

    // ��ȡѹ������
    fseek(in, 0, SEEK_END);
    long fileSize = ftell(in) - sizeof(HEAD);
    fseek(in, sizeof(HEAD), SEEK_SET);

    unsigned char* compressedData = (unsigned char*)malloc(fileSize);
    if (!compressedData) {
        cerr << "Memory allocation failed." << endl;
        fclose(in);
        return 0;
    }

    if (fread(compressedData, 1, fileSize, in) != fileSize) {
        cerr << "Failed to read compressed data." << endl;
        free(compressedData);
        fclose(in);
        return 0;
    }
    fclose(in);

    // Rebuild Huffman tree
    HTNode* pHT = (HTNode*)malloc(512 * sizeof(HTNode));
    memset(pHT, 0, 512 * sizeof(HTNode));
    CreateHuffmanTree(pHT, sHead.weight, 256);

    // Prepare output file
    string outputFilename = pFilename;
    size_t extPos = outputFilename.rfind(".huf");
    if (extPos != string::npos) {
        outputFilename.erase(extPos, 4);
        outputFilename = outputFilename + "_Decoded.bmp";
    }
    else {
        outputFilename = "decoded_" + string(pFilename);
    }

    cout << "Attempting to create output file: " << outputFilename << endl;
    FILE* out = fopen(outputFilename.c_str(), "wb");
    if (!out) {
        cerr << "Failed to create output file." << endl;
        free(compressedData);
        free(pHT);
        return 0;
    }

    // Decode the data
    int root = 511; // Root of Huffman tree
    int pos = root;
    int bytesDecoded = 0;

    for (long i = 0; i < fileSize && bytesDecoded < sHead.length; i++) {
        unsigned char byte = compressedData[i];

        for (int bit = 7; bit >= 0 && bytesDecoded < sHead.length; bit--) {
            // Test current bit
            bool isOne = (byte & (1 << bit)) != 0;

            // Navigate the tree
            pos = isOne ? pHT[pos].rchild : pHT[pos].lchild;

            // ���߽�����
            if (pos <= 0 || pos >= 512) {
                cerr << "Error: Invalid tree node index " << pos << endl;
                fclose(out);
                free(compressedData);
                free(pHT);
                return 0;
            }
            // If we reached a leaf node
            if (pHT[pos].lchild == 0 && pHT[pos].rchild == 0) {
                char symbol = (char)(pos - 1);
                fputc(symbol, out);
                bytesDecoded++;
                pos = root; // Reset to root for next symbol
            }
        }
    }

    fclose(out);
    free(compressedData);
    free(pHT);

    if (bytesDecoded != sHead.length) {
        cerr << "Warning: Decoded size (" << bytesDecoded
            << ") doesn't match expected size (" << sHead.length << ")" << endl;
    }
    else {
        cout << "Successfully decompressed " << bytesDecoded << " bytes." << endl;
    }

    return (bytesDecoded == sHead.length) ? 1 : 0;
}

int Encode(const char* pFilename, const HuffmanCode pHC, char* pBuffer, const int nSize)
{
    FILE* in = fopen(pFilename, "rb");
    if (!in) {
        cerr << "Failed to open file for coding" << endl;
        return 0;
    }

    memset(pBuffer, 0, nSize); // Initialize buffer with zeros

    char cd[256] = { 0 };
    int pos = 0;
    int ch;
    while ((ch = fgetc(in)) != EOF) {
        strcat(cd, pHC[ch]);
        while (strlen(cd) >= 8) {
            pBuffer[pos++] = Str2byte(cd);
            memmove(cd, cd + 8, strlen(cd) - 8 + 1);  // ʹ��memmove�����ڴ��ص�
        }
    }
    if (strlen(cd) > 0) {
        // ���Ϊ8λ
        while (strlen(cd) < 8) {
            strcat(cd, "0");  // ��0���
        }
        pBuffer[pos++] = Str2byte(cd);
    }
    fclose(in);

    return 1;
}

int WriteFile(const char* pFilename, const HEAD sHead, const char* pBuffer, const int nSize){
    char filename[256] = { 0 };
    strcpy(filename, pFilename);
    strcat(filename, ".huf");

    FILE* out = fopen(filename, "wb");
    fwrite(&sHead, sizeof(HEAD), 1, out);
    fwrite(pBuffer, sizeof(char), nSize, out);
    fclose(out);
    out = NULL;
    cout << "����ѹ���ļ�: " << filename << endl;
    int len = sizeof(HEAD) + nSize;
    return len;
}

char Str2byte(const char* pBinStr)
{
    char b = 0x00;
    for (int i = 0; i < 8; i++) {
        b = b << 1;
        if (pBinStr[i] == '1') {
            b = b | 0x01;
        }  
    }
    return b;
}


