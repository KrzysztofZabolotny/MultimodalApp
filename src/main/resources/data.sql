insert into user (id, user_name, password, active, roles)values
( 1, 'foo','foo','true','USER' ),
( 2, 'bar','bar','true','USER' );

insert into client(id,user_name,first_name,last_name,phone,email) values
(1,'foo','Krzysztof','Zabolotny','92010437','krzysztof@gmail.com'),
(2,'bar','Marian','Olejnik','2910437','marian@gmail.com');