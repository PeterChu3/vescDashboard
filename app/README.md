# VESC Dashboard

## Background & Explanation

![Trampa view](https://github.com/PeterChu3/vescDashboard/blob/Dashboard/Docs/Images/OsaTrampa.jpg?raw=true)

I have a friend, Osa All, who rides a trampa with a burner android phone to serve as a dashboard for his ride. He uses the stock VESC tool app for live speed and battery data. In my opinion the stock VESC-Tool telemtry page is rather cluttered for riding. According to Osa, speed and battery info at a glance is all he really needs and this app provides that in a easier to read way.

### Helpful links

The commands for VESC data
https://github.com/vedderb/bldc/blob/805006f03e486506145e6faa1f334d47fe9875d8/comm/commands.c

Helpful VESC packet functions
https://github.com/vedderb/bldc/blob/805006f03e486506145e6faa1f334d47fe9875d8/comm/packet.c

BLE/Gatt overview
https://learn.adafruit.com/introducing-the-adafruit-bluefruit-le-uart-friend/gatt-service-details

Forum post for VESC packets
http://vedder.se/2015/10/communicating-with-the-vesc-using-uart/

### Telemtry Packets & VESC information

##### General VESC UART packet Data

The VESC communicates over UART using packets even over BLE. The structure is in the following format for a general packet.

- 1 start byte with the value of 2 (short packets <= 255 bytes) or 3 (long packets > 255 bytes)
- 1-2 bytes specifying the packet PAYLOAD length
- ? bytes payload of the packet
- Two byte CRC checksum
- 1 stop byte value of 3

##### VESC Dashboard Request Data packet.

This app will request two values from COMM_GET_VALUES_SELECTIVE. This will minimize the size of the responce from the VESC because we only need the ERPM and battery voltage values.

A bitmask is used to select the values we have. A 32byte bitmasked is send over UART. The 8th & 9th digit of the bitmask should request ERPM and Voltage.

The payload size is 1 byte (to request selective values) + 4 bytes (the bitmask). Total payload: 5 bytes

ERPM - 32bit uint value
Battery Voltage - 16 bit unsighed int divided by 10.

The packet sent will have the following values

- 0x02 - start byte (short packet value)
- 0x05 - packet PAYLOAD length(TBD)
- 0x32 - packet PAYLOAD bit 1 (requests) selective data
- 0xXX - packet PAYLOAD MASK 1
- 0xXX - packet PAYLOAD MASK 2
- 0xXX - packet PAYLOAD MASK 3
- 0xXX - packet PAYLOAD MASK 4
- 0xXX - CRC
- 0xXX - CRC
- 0x03 - Stop byte

The packet reply will have the following values

- 0x02 - start byte (short packet value)
- 0x0? - packet PAYLOAD length(TBD)
- 0x32 - packet PAYLOAD bit 1 (Repies) selective data
- 0xXX - packet PAYLOAD MASK 1
- 0xXX - packet PAYLOAD MASK 2
- 0xXX - packet PAYLOAD MASK 3
- 0xXX - packet PAYLOAD MASK 4
- 0xXX - packet PAYLOAD ERPM 1
- 0xXX - packet PAYLOAD ERPM 2
- 0xXX - packet PAYLOAD ERPM 3
- 0xXX - packet PAYLOAD ERPM 4
- 0xXX - packet PAYLOAD Bvolt 1
- 0xXX - packet PAYLOAD Bvolt 2
- 0x03 - Stop byte
