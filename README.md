


Installation

1. Start OpenFire server using Docker:
    `docker run --name openfire -d --publish 9090:9090 --publish 5222:5222 --publish 7777:7777 sameersbn/openfire:3.10.3-19`
2. Access URL: http://localhost:9090/
3. Set the following configuration parameters:
    1. Create 3 user accounts:
        1. sniper: sniper
        2. auction-item-54321: auction
        3. auction-item-65432: auction
    2. Not store offline messages
    3. Set System Name to localhost
    4. Resource policy “Never kick”