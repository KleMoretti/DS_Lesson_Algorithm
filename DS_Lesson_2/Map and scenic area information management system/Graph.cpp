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

void CGraph::DFSTraverse(int nVex, PathList& pList)
{
	int nIndex = 0;
	bool aVisted[MAX_VERTEX_NUM] = { false };
	DFS(nVex, aVisted, nIndex, pList);
}

int CGraph::FindShortPath(int nVexStart, int nVexEnd, Edge aPath[])
{
	int nShortPath[MAX_VERTEX_NUM][MAX_VERTEX_NUM];
	int nShortDistance[MAX_VERTEX_NUM];
	bool aVisited[MAX_VERTEX_NUM];
	int v;

	for (v = 0; v < m_nVexNum; v++) {
		aVisited[v] = false;
		if (m_aAdjMatrix[nVexStart][v]) {
			nShortDistance[v] = m_aAdjMatrix[nVexStart][v];
		}
		else {
			nShortDistance[v] = 0x7FFFFFFF;
		}

		nShortPath[v][0] = nVexStart;
		for (int w = 1; w < m_nVexNum; w++) {
			nShortPath[v][w] = -1;
		}
	}

	aVisited[nVexStart] = true;
	int min;
	for (int i = 1; i < m_nVexNum; i++) {
		min = 0x7FFFFFFF;
		bool bAdd = false;
		for (int w = 0; w < m_nVexNum; w++)
		{
			if (!aVisited[w]) {
				if (nShortDistance[w] < min) {
					v = w;
					min = nShortDistance[w];
					bAdd = true;
				}
			}
		}
		if (!bAdd) {
			break;
		}
		aVisited[v] = true;
		nShortPath[v][i] = v;

		for (int w = 0; w < m_nVexNum; w++) {
			if (!aVisited[w] && (min + m_aAdjMatrix[v][w]) < nShortDistance[w] && (m_aAdjMatrix[v][w] > 0)) {
				nShortDistance[w] = min + m_aAdjMatrix[v][w];
				for (int i = 0; i < m_nVexNum; i++) {
					nShortPath[w][i] = nShortPath[v][i];
				}
			}
		}
	}

	int nIndex = 0;
	int nVex1 = nVexStart;

	// 将最短路径保存到边的结构体数组中
	for (int i = 1; i < m_nVexNum; i++)
	{
		if (nShortPath[nVexEnd][i] != -1)
		{
			aPath[nIndex].vex1 = nVex1;
			aPath[nIndex].vex2 = nShortPath[nVexEnd][i];
			aPath[nIndex].weight = m_aAdjMatrix[nVex1][aPath[nIndex].vex2];
			nVex1 = nShortPath[nVexEnd][i];
			nIndex++;
		}
	}
	return nIndex;
}

void CGraph::FindMinTree(Edge aPath[])
{
	bool aVisited[MAX_VERTEX_NUM];
	for (int i = 0; i < MAX_VERTEX_NUM; i++) {
		aVisited[i] = false;
	}
	aVisited[0] = true;
	int min;
	int nVex1, nVex2;
	for (int k = 0; k < m_nVexNum - 1; k++) {
		min = 0x7FFFFFFF;
		for (int i = 0; i < m_nVexNum; i++) {
			if (aVisited[i]) {
				for (int j = 0; j < m_nVexNum; j++) {
					if (!aVisited[j]) {
						if ((m_aAdjMatrix[i][j] < min) && (m_aAdjMatrix[i][j] != 0)) {
							nVex1 = i;
							nVex2 = j;
							min = m_aAdjMatrix[i][j];
						}
					}
				}
			}
		}
		aPath[k].vex1 = nVex1;
		aPath[k].vex2 = nVex2;
		aPath[k].weight = m_aAdjMatrix[nVex1][nVex2];
		aVisited[nVex1] = true;
		aVisited[nVex2] = true;
	}
}

void CGraph::DFS(int nVex, bool aVisited[], int& nIndex, PathList& pList)
{
	aVisited[nVex] = true; // 已经访问过
	pList->vexs[nIndex++] = nVex;

	int nVexNum = 0;
	for (int i = 0; i < m_nVexNum; i++) // 搜索 nVex 的所有邻接点
	{
		if (aVisited[i])
		{
			nVexNum++;
		}
	}
	if (nVexNum == m_nVexNum) {
		pList->next = (PathList)malloc(sizeof(Path));
		for (int i = 0; i < m_nVexNum; i++) {
			pList->next->vexs[i] = pList->vexs[i];
		}
		pList = pList->next;
		pList->next = NULL;
	}
	else {
		for (int i = 0; i < m_nVexNum; i++) {
			if ((!aVisited[i]) && (m_aAdjMatrix[nVex][i] > 0)) {
				DFS(i, aVisited, nIndex, pList);

				aVisited[i] = false;
				nIndex--;
			}
		}//for
	}
}