# MagicMirrorApp

This is an Android App that allows you to connect to a [MagicMirror](https://github.com/MichMich/MagicMirror). This can either be done via BLE or Wifi (indirectly).


## Video:

This video is relatively old and will be updated sometime.

[![IMAGE ALT TEXT HERE](https://img.youtube.com/vi/hUUipBgShb4/0.jpg)](https://www.youtube.com/watch?v=hUUipBgShb4)

## Requirements

### BLE Connectivity

- [ble_app_interface](https://github.com/aschulz90/ble_app_interface) installed on your MagicMirror
- a Bluetooth 4.0 capable Smartphone

This app uses [SweetBle](http://idevicesinc.com/sweetblue/) for managing its BLE-functionality.

### Wifi Connectivity

- [MMM-Remote-Control](https://github.com/Jopyth/MMM-Remote-Control) installed on your MagicMirror
- the MagicMirror and your Smartphone need to be in the same network 
- your Smartphone needs to be whitelisted on the MagicMirror

## Permissions

Permission | Used for | Required
--- | --- | ---
ACCESS_WIFI_STATE | checking, if Wifi is enabled | yes
ACCESS_NETWORK_STATE | scanning for MagicMirror in the local network | yes
INTERNET | communicating with a MagicMirror over Wifi | yes
BLUETOOTH | connecting to the MagicMirror via BLE | yes
BLUETOOTH_ADMIN | checking, if Bluetooth is enabled | yes
ACCESS_COARSE_LOCATION | scanning for nearby MagicMirrors via BLE | yes
WRITE_EXTERNAL_STORAGE | writing log files in case of an app crash | no

## Usage

### Choose an Adapter

BLE is the default adapter, if you want to change it:

- open the side menu and click on "App Setting"
- click on "MagicMirror Adapter" and select your desired adapter

### Connect to a MagicMirror

- open the side menu
- click on "Scan" in the top right corner to start scanning for nearby MagicMirrors
- after a MagicMirror was detected, it will show up in the list
- click on the MagicMirror in the list you want to connect to

### Change between paired MagicMirrors

- open the side menu
- click on the spinner at the top and select another MagicMirror
- OR got to "App Settings" and click on "Paired Mirror" and select another MagicMirror

### Execute Actions

- open the side menu
- select "MagicMirror Settings"
- scroll down to the "Queries" section and click on the action you want to execute

## License

Copyright (C) 2017  Andreas Schulz

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
