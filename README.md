MockLocation
================================================================================

refs: http://docs.sumile.cn/android/training/location/location-testing.html

Update location from adb.

HOW TO USE
--------------------------------------------------------------------------------

### Prerequisites

1. Enable Developer Options on your Android device
2. Select this app as the mock location app in Developer Options
3. Launch the app and grant the following permissions:
   - Location permission (ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION)
   - Background location permission (ACCESS_BACKGROUND_LOCATION) - Select "Allow all the time"

### Update Mock Location via ADB

Use the following command to update mock location from command line:

    $ adb shell am broadcast -a org.organlounge.mocklocation.UPDATE_MOCK_LOCATION \
        -n org.organlounge.mocklocation/.MockLocationReceiver \
        --ef lat_from 35.6485513 --ef lon_from 139.713038 \
        --ef lat_to 35.6484069 --ef lon_to 139.7166082 \
        --ei duration 20 --ei times 10

Parameters:
- `lat_from`: Starting latitude
- `lon_from`: Starting longitude
- `lat_to`: Ending latitude
- `lon_to`: Ending longitude
- `duration`: Interval between updates in seconds
- `times`: Number of steps to interpolate between start and end positions
