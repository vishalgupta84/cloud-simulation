#include <bits/stdc++.h>
using namespace std;
int N,M;	//N=number of tasks , M= number of processors
int effThreshold=0;

class schedule{
public:
	int scheduledtasks;
	std::vector<float> length;
	std::vector<float> mips;
	vector< vector<float> > table;
	vector< vector<float> > grid ;
	vector< vector<float> > efficiency ;
	vector< int > avaiable;
	schedule(){
		table.resize(M);
		scheduledtasks=0;
	}
	void setGrid(int N,int M){
		length.resize(N);
		mips.resize(M);
		avaiable.resize(M);
		efficiency.resize(M);
		grid.resize(M);
		for(int i=0;i<N;i++){
			int task_size=rand()%20000+20000;
			length[i]=(float)task_size;
		}
		for(int i=0;i<M;i++){
			int processor_mips=rand()%512+512;
			mips[i]=(float)processor_mips;
		}
		for(int i=0;i<M;i++){
			vector<float> temp(N);
			vector<float> temp2(N);
			efficiency[i] = temp2;
			grid[i] = temp;
		}
		for(int i=0;i<M;i++){
			for(int j=0;j<N;j++){
				grid[i][j] = (float)(length[j]/mips[i]);	
			}
		}
	}
	void fillEfficiency(){
		vector<float> gridmin(M);
		for(int i=0;i<M;i++){
			gridmin[i]=INT_MAX;
			for(int j=0;j<N;j++)
				gridmin[i] = min(gridmin[i],grid[i][j]);
		}

		for(int i=0;i<M;i++)
			for(int j=0;j<N;j++)
				efficiency[i][j] = (float)gridmin[i]/grid[i][j];
	}
	int getMinPmin(){
		int pmin=0;
		for(int i=0;i<M;i++)
			if(avaiable[i] < avaiable[pmin])
				pmin=i;
		return pmin;
	}
	int getEffMax(int pmin){
		int effmax=0;
		for(int j=0;j<N ;j++)
			if(efficiency[pmin][j] > efficiency[pmin][effmax])
				effmax = j;
		return effmax;
	}

	bool set(int pmin,int effmax){
		if(efficiency[pmin][effmax] < effThreshold){
			avaiable[pmin] = INT_MAX;
			return true;
		}
		for(int i=0;i<M;i++)	
			efficiency[i][effmax] = INT_MIN;
		return false;
	}

	void addtask(int processors_no,int task_no){
		scheduledtasks++;
		table[processors_no].push_back(task_no);
		avaiable[processors_no] += grid[processors_no][task_no];
	} 

	bool completed(){
		if(scheduledtasks <N)
			return false;
		return true;
	}

	void print(){
		cout<<"----------------------"<<endl;
		cout<<"schedule"<<endl;
		cout<<"----------------------"<<endl;
		float Cmax=INT_MIN;
		
		for(int i=0;i<M;i++){
			float pi=0;
			cout<<"Processor "<<i<<" :";
			for(int j=0;j<table[i].size();j++){
				pi += grid[i][table[i][j]];
				cout<<table[i][j]<<" ";
			}
			cout<<" --- running time = "<<pi<<endl;
			Cmax = max(Cmax,pi);
			// cout<<endl;
		}
		cout<<"Cmax = "<<Cmax<<endl;
	}
	
	void showTask(){
		cout << "Total number of Task=  " << N << endl;
		for (int i = 0; i < N; ++i){
			cout <<  length[i] << " ";
		}
		cout << endl;
	}

	void showMips(){
		cout << "Total number of processors=  " << M << endl;
		for (int i = 0; i < M; ++i){
			cout <<  mips[i] << " ";
		}
		cout << endl;	
	}

};


int main(){
	srand (time(NULL));
	cout<<"Enter number of processors and tasks respectively"<<endl;
	cin>>M>>N;
	schedule scheduler;
	scheduler.setGrid(N,M);
	scheduler.fillEfficiency();
	while(!scheduler.completed()){
		int pmin=scheduler.getMinPmin();
		int effmax=scheduler.getEffMax(pmin);
		if(scheduler.set(pmin,effmax))
			continue;
		scheduler.addtask(pmin,effmax);

	}
	scheduler.print();
	return 0;
}