pragma solidity >=0.4.22 <0.6.0;

contract SmartSociety {

    address[] private smartSocietyApplications;
    address private owner;

    constructor () public{
        owner = msg.sender;
    }

    modifier restricted(){
        require(msg.sender == owner);
        _;
    }

    event SmartSocietyApp(address _address, address _from, uint256 _money);

    function createSmartSocietyApplication(address payable[] calldata voters, uint256 voterPrice,
        address payable[] calldata workers, uint256 workerPrice, address payable[] calldata substitutes, uint256 substitutePrice, bytes32[] calldata _tasks) external
    payable returns (string memory){

        SmartSocietyApplication smartSocietyApplication = (new SmartSocietyApplication).value(msg.value)(_tasks, owner);
        smartSocietyApplication.setData(voters, voterPrice, workers, workerPrice, substitutes, substitutePrice);
        address smartSocietyApplicationAddress = address(smartSocietyApplication);
        smartSocietyApplications.push(smartSocietyApplicationAddress);
        emit SmartSocietyApp(smartSocietyApplicationAddress, msg.sender, msg.value);
    }

    function getSmartSocietyApplications() public view returns(address[] memory _addresses){
        _addresses = smartSocietyApplications;
    }

}

contract SmartSocietyApplication {

    struct Task {
        bool complete;
        uint approvals;
        bytes32 id;
    }

    Task[] tasks;

    address public smartSocietyOwner;
    address public factoryAddress;

    address payable[] voters;
    address payable[] workers;
    address payable[] substitutes;

    uint256 voterPrice;
    uint256 workerPrice;
    uint256 substitutePrice;

    uint private completedTasks;
    bool private open = true;

    mapping(uint => bool) private approvers;
    uint private totalVotes;
    event QualityAssurance(bytes32 id, uint256 pro, uint256 against);


    modifier authenticated(){
        require(msg.sender == smartSocietyOwner);
        _;
    }
    event Print(address owner, address sender);
    modifier restricted(){
        require(msg.sender == factoryAddress, "we been knew");
        _;
    }

    function setData(address payable[] calldata _voters, uint256 _voterPrice, address payable[] calldata _workers, uint256 _workerPrice,
        address payable[] calldata _substitutes, uint256 _substitutePrice) external restricted returns (address, address){
        voters = _voters;
        workers = _workers;
        substitutes = _substitutes;

        voterPrice = _voterPrice * 1 wei;
        workerPrice = _workerPrice * 1 wei;
        substitutePrice = _substitutePrice * 1 wei;

        assert(( (_voters.length * _voterPrice) + (_workers.length * _workerPrice) + (_substitutes.length * _substitutePrice)) <= address(this).balance);
        return (smartSocietyOwner, msg.sender);
    }

    constructor(bytes32[] memory _tasks, address _smartSocietyOwner) public payable {
        factoryAddress = msg.sender;
        smartSocietyOwner = _smartSocietyOwner;
        for(uint i = 0; i < _tasks.length; i++)
            tasks.push(Task({
                complete: false,
                approvals: 0,
                id: _tasks[i]
                }));
    }

    function vote(uint _voter, uint _index, uint _approval) public authenticated{
        require(_index < tasks.length && tasks[_index].complete && open);
        require(_voter < voters.length && !approvers[_voter]);
        require(0 <= _approval && 1 >= _approval);
        ++totalVotes;
        approvers[_voter] = true;
        tasks[_index].approvals +=  _approval;

        if(totalVotes == voters.length)
            finalizeQualityAssurance();

    }

    function finalizeQualityAssurance() public authenticated {
        require(completedTasks == tasks.length);
        open = false;

        if(tasks.length > 0)
            quickSort(tasks, 0, tasks.length - 1);

        emit QualityAssurance(tasks[0].id, tasks[0].approvals, totalVotes);
    }

    function getResult() public view returns (bytes32 _id, uint _approvals, uint _totalVotes) {
        require(!open);
        _id = tasks[0].id;
        _approvals = tasks[0].approvals;
        _totalVotes = totalVotes;
    }

    function completeTask(uint _index) public authenticated {
        tasks[_index].complete = true;
        completedTasks++;
    }

    function quickSort(Task[] storage  _tasks, uint left, uint right) internal {
        uint i = left;
        uint j = right;
        uint pivot = _tasks[left + (right - left) / 2].approvals;
        while (i <= j) {
            while (_tasks[i].approvals < pivot) i++;
            while (pivot < _tasks[j].approvals) j--;
            if (i <= j) {
                (_tasks[i].approvals, _tasks[j].approvals) = (_tasks[j].approvals, _tasks[i].approvals);
                i++;
                j--;
            }
        }
        if (left < j)
            quickSort(_tasks, left, j);
        if (i < right)
            quickSort(_tasks, i, right);
    }

    function payPeers(address payable [] memory peers, uint256 price) public authenticated {
        for(uint i = 0; i < peers.length; i++)
            peers[i].transfer(price);
    }

    function test() public view authenticated returns (uint256) {
        return address(this).balance;
    }

}
