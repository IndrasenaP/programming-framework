pragma solidity >=0.4.22 <0.6.0;

contract CollectiveBasedTaskContractFactory {

    mapping(address => bool) private collectiveBasedTasks;
    address private owner;
    uint private minimum;

    constructor (uint _minimum) public{
        require(_minimum > 0, "The minimum value should be higher than 0.");
        owner = msg.sender;
        minimum = _minimum;
    }

    modifier restricted(){
        require(msg.value >= minimum, "insufficient money");
        _;
    }

    event CollectiveBasedTask(address _address, address _from, uint256 _money);

    function createCollectiveBasedTask() external restricted
    payable returns (address) {

        CollectiveBasedTaskContract cbt = (new CollectiveBasedTaskContract).value(msg.value)(owner);
        address cbtAddress = address(cbt);
        collectiveBasedTasks[cbtAddress] = true;
        emit CollectiveBasedTask(cbtAddress, msg.sender, msg.value);
        return cbtAddress;
    }

    function getCollectiveBasedTasks(address _address) public view returns(bool){
        return collectiveBasedTasks[_address];
    }

}

contract CollectiveBasedTaskContract {


    address private smartSocietyOwner;

    modifier authenticated(){
        require(msg.sender == smartSocietyOwner, "not authorized");
        _;
    }

    event Print(address owner, address sender);

    constructor(address _smartSocietyOwner) public payable {
        smartSocietyOwner = _smartSocietyOwner;
    }


    function payPeers(address payable [] memory peers, uint256 price) public authenticated{

        require(address(this).balance >= (peers.length * price), "insufficient money");

        for(uint i = 0; i < peers.length; i++)
            peers[i].transfer(price);
    }

}