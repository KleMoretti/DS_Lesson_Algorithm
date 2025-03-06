#include "Tourism.h"
#include<iostream>
using namespace std;

#define VEX_FILE_NAME "Vex.txt"
#define EDGE_FILE_NAME "Edge.txt"

CTourism::CTourism(void)
{
}

CTourism::~CTourism(void)
{
}

void CTourism::CreateGraph()
{
	cout << "====Create Scenic Map====" << endl;
	m_Graph.Init();

	FILE* pVex;
	errno_t err = fopen_s(&pVex, VEX_FILE_NAME, "r");
	if (err != 0) {
		cout << "Open file failed!" << endl;
		return;
	}

	int nSpotCount = 0;
	if (fscanf_s(pVex, "%d\n", &nSpotCount) != 1) {
		cout << "Read spot count failed!" << endl;
		fclose(pVex);
		return;
	}
	cout << "Vex Number: " << nSpotCount << endl;

	cout << "----- Vex ----" << endl;
	Vex sVex;
	while (fscanf_s(pVex, "%d\n%s\n%s\n", &sVex.num, sVex.name, 
		(unsigned)_countof(sVex.name), sVex.desc, (unsigned)_countof(sVex.desc)) != EOF) {
		cout << sVex.num << "-" << sVex.name << endl;
		if (!m_Graph.InsertVex(sVex)) {
			cout << "Vex addition failure!" << endl;
			continue;
		}
	}
	fclose(pVex);
	pVex = NULL;

	FILE* pEdge;
	err = fopen_s(&pEdge, EDGE_FILE_NAME, "r");
	if (err != 0) {
		cout << "Open file failed!" << endl;
		return;
	}
	cout << "---- Edge ----" << endl;
	Edge sEdge;
	while (fscanf_s(pEdge, "%d\t%d\t%d\n", &sEdge.vex1, &sEdge.vex2, &sEdge.weight) != EOF) {
		cout << "(v" << sEdge.vex1 << ",v" << sEdge.vex2 << ")   " << sEdge.weight << endl;
		if (!m_Graph.InsertEdge(sEdge)) {
			cout << "Edge addition failure!" << endl;
			continue;
		}
	}
	fclose(pEdge);
	pEdge = NULL;
}

void CTourism::GetSpotInfo()
{
	cout << "==== Search Scenic Spot Info" << endl;
	int nVexNum = m_Graph.GetVexNum();
	for (int i = 0; i < nVexNum; i++) {
		Vex sVex = m_Graph.GetVex(i);
		cout << sVex.num << "-" << sVex.name << endl;
	}

	int nVex;
	cout << "Please input the number you want to query: ";
	cin >> nVex;
	if (nVex < 0 || nVex >= nVexNum) {
		cout << "Error Input!\n";
		return;
	}
	Vex sVex = m_Graph.GetVex(nVex);
	cout << sVex.name << endl;
	cout << sVex.desc << endl;
	
	Edge aEdge[MAX_VERTEX_NUM];
	int nEdgeNum = m_Graph.FindEdge(nVex, aEdge);
	cout << "---- Surrounding Scenic Spot ----" << endl;
	for (int i = 0; i < nEdgeNum; i++) {
		Vex v1 = m_Graph.GetVex(aEdge[i].vex1);
		Vex v2 = m_Graph.GetVex(aEdge[i].vex2);
		cout << v1.name << "->" << v2.name << " " << aEdge[i].weight << "m" << endl;
	} 
}
