MockLocation
================================================================================

refs: http://docs.sumile.cn/android/training/location/location-testing.html

update location from adb.

HOW TO USE
--------------------------------------------------------------------------------

    $ adb shell am startservice -a org.organlounge.mocklocation.UPDATE_MOCK_LOCATION --ef lat_from 35.6485513 --ef lon_from 139.713038 --ef lat_to 35.6484069 --ef lon_to 139.7166082 --ei duration 20 --ei times 10
