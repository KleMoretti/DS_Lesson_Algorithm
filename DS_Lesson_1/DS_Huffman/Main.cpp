#include<iostream>
#include <cstdio>
#include "Huffman.h"
#include"Compress.h"
using namespace std;


int main() {
    cout << "=========Huffman Compression Tool=========" << endl;
    cout << "1. Compress\n2. Decompress\nEnter choice: ";
    int choice;
    cin >> choice;
    cin.ignore(); // Çå³ıÊäÈë»º³å

    cout << "Enter filename: ";
    char filename[256];
    cin.getline(filename, 256);

    int result = 0;
    if (choice == 1) {
        result = Compress(filename);
       
    }
    else if (choice == 2) {
        result = Decompress(filename);
    }
    else {
        cout << "Invalid choice." << endl;
        return 1;
    }

    cout << (result ? "Success!" : "Failed!") << endl;
    return 0;
}