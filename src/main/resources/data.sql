insert into user (id, user_name, password, active, roles)
values (1, 'kz', 'kz', true, 'USER'),
       (2, 'st', 'st', true, 'USER');

insert into client(id, user_name, name, surname, street, city, zip, email, password, code, phone, role, company_name)
values (1, 'kz', 'Krzysztof', 'Zabolotny', 'Konows Gate 1E', 'Oslo', '0192', 'kz',
        'kz', '+47', '82010437', 'Client', 'none'),
       (2, 'st', 'Stefan', 'Serafin', 'Birken Gate 1E', 'Tromso', '0992', 'st',
        'st', '+47', '92010437', 'Driver', 'Speedy Transport');

insert into transport (id, departure_date, destination, driver_id, company_name, number_of_parcels, ballast, capacity, transport_value,value)
values ( 1, '2021-02-02', 'Tromso' , 'st', 'Speedy Transport','9', 0,0,0,0);

insert into price_range(from_weight, to_weight, price)
values (1,10,100);

/*
insert into transport (id, departure_date, destination, driver_id, company_name, number_of_parcels)
values ( 1, '2021-02-02', 'Tromso' , 'st', 'Speedy Transport','9'),
       ( 2, '2021-03-02', 'Oslo' , 'kalamaga@gmail.com', 'Kalamaga Przewozy' ,'9'),
       ( 3, '2021-01-04', 'Tromso' , 'st', 'Speedy Transport','3'),
       ( 4, '2021-05-14', 'Oslo' , 'stefan@gmail.com', 'Speedy Transport','8');


insert into parcel (id, user_name, content, weight, width, length, height, additional_comments, status, destination, departure_date)
values  (1, 'kz','Ubrania',10,20,30,40,'Uzywane ubrania','delivered','Oslo','2020-12-31'),
        (2, 'kz','Zabawki',10,20,30,40,'Uzywane zabawki','delivered','Drammen','2020-12-31'),
        (3, 'kz','Jedzenie',10,20,30,40,'Szynki, kielbasy','delivered','Ystad','2020-12-21'),
        (4, 'kz','Ksiazki',10,20,30,40,'Ksiazki dla biblioteki','transit','Stavanger','2020-12-04');
*/
