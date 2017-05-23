#include <iostream>
#include <vector>
#include <string>
#include <map>
#include <algorithm>
#include <iomanip>
#include <sstream>
using namespace std;

struct address {
	int relaAdd;
	int absoAdd;
    int numOfMod;
    bool used;
    address() : relaAdd(-1), absoAdd(-1), numOfMod(-1), used(false) {}
};

struct errInfo {
    int numOfMod;
    int line;
    //Type 1: an address appearing in a definition exceeds the size of the module. The relative address is treated as 0.
    //Type 2: a symbol is multiply defined. The value given in the first definition is used.
    //Type 3: a symbol is used but not defined. Use the value zero.
    //Type 4: multiple symbols used in the same instruction. Ignore all but the first usage given.
    //Type 5: the length of the symbol exceeds 8. Only the first 8 char is stored.
    int errorType;
    string symbol;
};

std::string to_string(int i)
{
    std::stringstream ss;
    ss << i;
    return ss.str();
}

vector<pair<string,address> > NDpairs;
vector<map<int,string> > NUpairs;
vector<vector<pair<string,int> > > NTpairs;
vector<int> moduleSize;
vector<errInfo> errorTable;

/**************************
Input the number of modules
**************************/
int modules() {
	string modules;
	cin >> modules;
	int numOfModules = 0;
	for( int i(0); i < modules.size(); ++i ) {
		if( isdigit(modules[i]) ) {
			numOfModules = numOfModules * 10 + modules[i] - '0';
		}
		else {
			return -1;
		}
	}
    return numOfModules;
}

/************************
Input the definition list
************************/
int definition ( int mod ) {
	string count;
	cin >> count;
	int ND = 0;
	for( int i(0); i < count.size(); ++i ) {
		if( isdigit(count[i]) ) {
			ND = ND * 10 + count[i] - '0';
		}
		else {
			return -2;
		}
	}
	for( int i(1); i <= ND; ++i ) {
		string symbol;
		cin >> symbol;
		int relativeAddress;
		cin >> relativeAddress;
        /******************************************
        Check if the length of the symbol exceeds 8
        ******************************************/
        if( symbol.size() > 8 ) {
            errInfo e;
            e.errorType = 5;
            string sym( symbol.begin(), symbol.begin() + 8 );
            e.symbol = sym;
            errorTable.push_back( e );
            symbol = sym;
        }
        bool exist = false;
        for( int j(0); j < NDpairs.size(); ++j ) {
            if( NDpairs[j].first == symbol ) {
                exist = true;
                bool inTable = false;
                for( int k(0); k < errorTable.size(); ++k ) {
                    if( errorTable[k].symbol == symbol ) {
                        inTable = true;
                        break;
                    }
                }
                if( !inTable ) {
                    /***************************
                    a symbol is multiple defined
                    ***************************/
                    errInfo e;
                    e.symbol = symbol;
                    e.errorType = 2;
                    errorTable.push_back( e );
                }
            }
        }
        if( !exist ) {
            address a;
            a.relaAdd = relativeAddress;
            a.numOfMod = mod;
            NDpairs.push_back(make_pair(symbol,a));
        }
	}
    return ND;
}

/*****************
Input the use list
*****************/
int usage ( int mod ) {
	string use;
	cin >> use;
	int numOfUsage = 0;
	for( int i(0); i < use.size(); ++i ) {
		if( isdigit(use[i]) ) {
			numOfUsage = numOfUsage * 10 + use[i] - '0';
		}
		else {
			return -3;
		}
	}
    map<int,string> m;
    for( int i(1); i <= numOfUsage; ++i ) {
        string symbol;
        cin >> symbol;
        int pos;
        cin >> pos;
        /******************************************
         Check if the length of the symbol exceeds 8
         ******************************************/
        if( symbol.size() > 8 ) {
            string sym( symbol.begin(), symbol.begin() + 8 );
            symbol = sym;
        }
        while( pos != -1 ) {
            //If the address is multiple used, reserve the first use.
            map<int,string>::iterator it = m.find( pos );
            if( it == m.end() )
                m.insert(make_pair(pos,symbol) );
            else{
                errInfo e;
                e.errorType = 4;
                e.numOfMod = mod;
                e.line = pos;
                errorTable.push_back(e);
            }
            cin >> pos;
        }
    }
    NUpairs.push_back(m);
	return numOfUsage;
}

/*********************
Input the program text
*********************/
int text () {
    string text;
    cin >> text;
    int numOfText = 0;
    for( int i(0); i < text.size(); ++i ) {
        if( isdigit(text[i]) ) {
            numOfText = numOfText * 10 + text[i] - '0';
        }
        else {
            return -4;
        }
    }
    vector<pair<string,int> > NT;
    for( int i(1); i <= numOfText; ++i ) {
        string programText;
        int address;
        cin >> programText >> address;
        NT.push_back(make_pair( programText, address ) );
    }
    NTpairs.push_back( NT );
    return numOfText;
}

/************************
 Compute the symbol table
 ***********************/
void symbolTable( vector<string> &symList ) {
    for( int i(1); i < moduleSize.size(); ++i ) {
        moduleSize[i] += moduleSize[i-1];
    }
    for( int i(0); i < NDpairs.size(); ++i ) {
        int baseAddress = moduleSize[NDpairs[i].second.numOfMod];
        NDpairs[i].second.absoAdd = NDpairs[i].second.relaAdd + baseAddress;
        //Ensure the address
        if( NDpairs[i].second.absoAdd < moduleSize[NDpairs[i].second.numOfMod+1] ) {
            symList[ NDpairs[i].second.absoAdd ] = NDpairs[i].first;
        }
        else {
            if( symList[baseAddress] == "" ) {
                symList[baseAddress] = NDpairs[i].first;
                errInfo e;
                e.errorType = 1;
                e.symbol = NDpairs[i].first;
                errorTable.push_back(e);
                NDpairs[i].second.relaAdd = 0;
                NDpairs[i].second.absoAdd = NDpairs[i].second.relaAdd + baseAddress;
            }
        }
        
    }
    cout << "Symbol Table" << endl;
    for( int i(0); i < symList.size(); ++i ) {
        if( symList[i] != "" ) {
            cout << symList[i] << "=" << i;
            for( int j(0); j < errorTable.size(); ++j ) {
                if( errorTable[j].errorType == 2  && errorTable[j].symbol == symList[i] ) {
                    cout << " Error: This variable is multiply defined; first value used.";
                }
                else if( errorTable[j].errorType == 1  && errorTable[j].symbol == symList[i] ) {
                    cout << " Error: Definition exceeds module size; first word in module used.";
                }
                else if( errorTable[j].errorType == 5  && errorTable[j].symbol == symList[i] ) {
                    cout << " Warning: The length of the symbol exceeds the limit 8. Only the first 8 char are stored.";
                }
            }
            cout << endl;
        }
    }
}

/*********************
Compute the Memory Map
*********************/
void memoryMap( vector<string> &symList ) {
    cout << "Memory Map" << endl;
    int count = 0;
    for( int i(0); i < NTpairs.size(); ++i ) {
        cout << "+" << count << endl;
        map<int,string>::iterator it = NUpairs[i].begin();
        for( int j(0); j < NTpairs[i].size(); ++j ) {
            string order = to_string( j ) + ":";
            if( symList[count] != "" ) {
                order += symList[count];
            }
            bool defined = false;
            bool absoAddError = false;
            bool relaAddError = false;
            string errSymbol;
            string pText = NTpairs[i][j].first + " ";
            string command = to_string(NTpairs[i][j].second);
            while( command.size() < 4 ) {
                command.insert( command.begin(), '0' );
            }
            pText += command;
            if( it != NUpairs[i].end() && it->first == j ) {
                pText += " ->";
                pText += it-> second;
                errSymbol = it->second;
                NTpairs[i][j].second = NTpairs[i][j].second/1000*1000;
                /*****************************
                Check if the symbol is defined
                *****************************/
                for( int k(0); k < NDpairs.size(); ++k ) {
                    if( it->second == NDpairs[k].first ) {
                        defined = true;
                        NTpairs[i][j].second += NDpairs[k].second.absoAdd;
                        NDpairs[k].second.used = true;
                    }
                }
                ++it;
            }
            string add;
            if( NTpairs[i][j].first == "R" ) {
                if( NTpairs[i][j].second % 1000 < moduleSize[i+1] ) {
                    string c1 = to_string(NTpairs[i][j].second);
                    while( c1.size() < 4 ) {
                        c1.insert( c1.begin(), '0' );
                    }
                    add = c1 + "+" + to_string( moduleSize[i]) + "=";
                    string c3 = to_string( NTpairs[i][j].second + moduleSize[i] );
                    while( c3.size() < 4 ) {
                        c3.insert( c3.begin(), '0' );
                    }
                    add += c3;
                }
                /*****************************************************************************
                relative address exceeds the size of the module. use the value zero (absolute)
                *****************************************************************************/
                else {
                    add = to_string( NTpairs[i][j].second/1000*1000 );
                    relaAddError = true;
                }
            }
            else if ( NTpairs[i][j].first == "E" ) {
                add = to_string(NTpairs[i][j].second);
                while( add.size() < 4 ) {
                    add.insert( add.begin(), '0' );
                }
            }
            else if ( NTpairs[i][j].first == "A" ) {
                if( NTpairs[i][j].second % 1000 >= 200 ) {
                    add = to_string(NTpairs[i][j].second / 1000 * 1000);
                    absoAddError = true;
                }
            }
            cout << left << setw(20) << order << left << setw(20) << pText << add;
            if( NTpairs[i][j].first == "E" && !defined ) {
                cout << " Error: "<< errSymbol << " is not defined; zero used.";
            }
            if( absoAddError ) {
                cout << " Error: Absolute address exceeds machine size; zero used.";
            }
            if( relaAddError ) {
                cout << " Error: Relative address exceeds module size; zero used.";
            }
            for( int k(0); k < errorTable.size(); ++k ) {
                if( errorTable[k].errorType == 4 && errorTable[k].line == j & errorTable[k].numOfMod == i ) {
                    cout << " Error: Multiple variables used in instruction; all but first ignored.";
                }
            }
            ++count;
            cout << endl;
        }
    }
}

/***********************
Output the error message
***********************/
void errorMessage() {
    //Defined but not used.
    for( int i(0); i < NDpairs.size(); ++i ) {
        if( !NDpairs[i].second.used ) {
            cout << "Warning: "<< NDpairs[i].first <<" was defined in module " << NDpairs[i].second.numOfMod+1 << " but never used." << endl;
        }
    }
    for( int i(0); i < NUpairs.size(); ++i ) {
        map<int,string>::iterator it = NUpairs[i].begin();
        while( it != NUpairs[i].end() ) {
            if( it->first >= moduleSize[i+1] ) {
                cout << "Error: Use of " << it->second <<" in module " << i+1 << " exceeds module size; use ignored." << endl;
            }
            ++it;
        }
    }
}

int main () {
	int numOfModules = modules();
    //The vector moduleSize is used to record the total size of modules defined before the current module.
    moduleSize.push_back(0);
    int NT;
	if( numOfModules < 0 ) {
		//Print the error message.
		cout << "The number of modules input is not numerical." << endl;
		cout << "The procedure has to be terminated." << endl;
		return numOfModules;
	}
	else {
		for( int i(0); i < numOfModules; ++i ) {
            int ND = definition(i);
            if( ND < 0 ) {
            	//Print the error message.
				cout << "The number of the definitions input in module no." << i << " is not numerical." << endl;
				cout << "The procedure has to be terminated." << endl;
				return ND;
            }
            int NU = usage(i);
            if( NU < 0 ) {
                //Print the error message.
                cout << "The number of the use list input in module no." << i << " is not numerical." << endl;
                cout << "The procedure has to be terminated." << endl;
                return NU;
            }
            int NTtemp = text();
            if( NTtemp < 0 ) {
                //Print the error message.
                cout << "The number of the program text input in module no." << i << " is not numerical." << endl;
                cout << "The procedure has to be terminated." << endl;
                return NTtemp;
            }
            moduleSize.push_back(NTtemp);
            NT += NTtemp;
		}
        if( NT == 0 ) {
            cout << "The module does not exist!" << endl;
        }
        else {
            vector<string> symbolList(NT,"");
            symbolTable( symbolList );
            cout << endl;
            memoryMap( symbolList );
            cout << endl;
            errorMessage();
        }
	}
}
