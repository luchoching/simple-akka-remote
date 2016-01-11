var net = require('net');

var client = new net.Socket();

client.connect(10000, '127.0.0.1', function(){
  console.log('Conected');
  client.write('Hello Server!! from Client');
  client.end();
});

client.on('data', function(data){
  console.log('Received: ' + data);
});

client.on('close', function(){
  console.log('Connection closed');
});
