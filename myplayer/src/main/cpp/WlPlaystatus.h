//
// Created by yangw on 2018-3-6.
//

#ifndef MYMUSIC_WLPLAYSTATUS_H
#define MYMUSIC_WLPLAYSTATUS_H


class WlPlaystatus {

public:
    bool exit = false;
    bool load = true;
    bool seek = false;
    bool pause = false;

public:
    WlPlaystatus();
    ~WlPlaystatus();

};


#endif //MYMUSIC_WLPLAYSTATUS_H
