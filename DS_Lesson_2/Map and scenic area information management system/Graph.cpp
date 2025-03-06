#include "Graph.h"

CGraph::CGraph(void)
{
}

CGraph::~CGraph(void)
{
}

void CGraph::Init()
{
	m_nVexNum = 0;
	for (int i = 0; i < MAX_VERTEX_NUM; i++) {
		for (int j = 0; j < MAX_VERTEX_NUM; j++) {
			m_aAdjMatrix[i][j] = 0;
		}
	}
}

bool CGraph::InsertVex(Vex sVex)
{
	if (m_nVexNum == MAX_VERTEX_NUM) {
		return false;
	}

	m_aVexs[m_nVexNum++] = sVex;
	return true;
}

bool CGraph::InsertEdge(Edge sEdge)
{
	if (sEdge.vex1 < 0 || sEdge.vex1 >= m_nVexNum || sEdge.vex2 < 0 || sEdge.vex2 >= m_nVexNum) {
		return false;
	}

	m_aAdjMatrix[sEdge.vex1][sEdge.vex2] = sEdge.weight;
	m_aAdjMatrix[sEdge.vex2][sEdge.vex1] = sEdge.weight;
	return true;
}

Vex CGraph::GetVex(int nVEx)
{
	return m_aVexs[nVEx];
}

int CGraph::FindEdge(int nVex, Edge aEdge[])
{
	int k = 0;
	for (int i = 0; i < m_nVexNum; i++) {
		if (m_aAdjMatrix[nVex][i] != 0) {
			aEdge[k].vex1 = nVex;
			aEdge[k].vex2 = i;
			aEdge[k].weight = m_aAdjMatrix[nVex][i];
			k++;
		}
	}
	return k;
}


int CGraph::GetVexNum()
{
	return m_nVexNum;
}

void CGraph::DFSTraverse(int nVex, PathList& List)
{
}

int CGraph::FindShortPath(int nVexStart, int nVexEnd, Edge aPath[])
{
	return 0;
}

void CGraph::FindMinTree(Edge aPath[])
{
}

void CGraph::DFS(int nVex, bool aVisited[], int& nIndex, PathList& List)
{
}
