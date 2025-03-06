#include <iostream>
#include"Graph.h"
#include"Tourism.h"
using namespace std;


int main(void) {
	CTourism tourism;
	bool bRunning = true;
	char key[10] = { 0 };
	while (bRunning) {
		cout << "=====Scenic Area Information Manager System====" << endl;
		cout << "1. Create Scenic scenic-map" << endl;
		cout << "2. Search scenic spot information" << endl;
		cout << "3. Tourist spot navigation" << endl;
		cout << "4. Search shorteat path" << endl;
		cout << "5. Laying circuit planning" << endl;
		cout << "0. Exit" << endl;

		cout << "Please input your choice(0~5): ";
		cin >> key;
		switch (key[0]) {
		case '1':
			tourism.CreateGraph();
			break;
		case '2':
			tourism.GetSpotInfo();
			break;
		case '3':
			break;
		case '4':
			break;
		case '5':
			break;
		case '0':
			bRunning = false;
			cout << "GoodBye!" << endl;
			break;
		}

	}
	return 0;
}