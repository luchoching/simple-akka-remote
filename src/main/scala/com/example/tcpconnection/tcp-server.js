var net = require('net');

var server = net.createServer(function(socket){
  socket.write('Hello from Nodejs TCP Server\r\n');
  socket.on('data', function(data){
    console.log(data.toString());
    //socket.write(data.toString());
  });
});

server.listen(10000, '127.0.0.1');

