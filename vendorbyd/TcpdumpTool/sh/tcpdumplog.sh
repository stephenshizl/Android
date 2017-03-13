#!/system/bin/sh
mkdir /sdcard/Tcpdump
/system/xbin/tcpdump -i any -s 0 -w /sdcard/Tcpdump/`date +%Y_%m_%d_%H_%M_%S`.pcap
/system/bin/setprop "persist.service.tcpdump.enable" 0
