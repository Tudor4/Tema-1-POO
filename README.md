# Tema1 - POO
# Niculescu Tudor-Alexandru 321CA

Detalii generale:
    
    -> Scheletul problemei se afla in metoda action din clasa main. Aceasta preia fiecare 		comandata din lista de actiuni, determina tipul comenzii iar apoi apeleaza metoda 		corespunzatoare. Dupa ce metoda a fost executat se creeaza un nou obiect JSON 		completat cu id-ul comenzii si mesajul corespunzator(eroare, succes, 		    query result, recommendation result).
    
    -> Fiecare comanda, query sau recomandare are propria sa metoda dedicata ce va fi apelata.
    
    -> Aceste metode sunt explicat pe scurt in fisierele sursa in comentariile javadoc aferente.
    
    -> Toate varibilele si metodele au nume sugestive, prin urmare nu am mai pus comentarii in 	dreptul acestor campui in cod.
    
     

Stocarea datelor:

    -> Datele sunt stocate in clasele din pachetul data. Am creat clase noi: Actor, Data, 		Movie, Serial, Show, User. User stocheaza toate datele pentru useri, Movie pentru 		filme, Serial pentru seriale, Actor pentru actori iar Show este clasa abstracta 		extinsa de Movie si Serial. Data este clasa ce contine listele cu utilizatori, actori 		si videoclipuri(clasa esentiala a acestei teme).
    
    -> Clasele mentionate mai sus sunt create dupa modelul celor deja implementate, la care am 	adaugat variabile noi si metodele ce rezolva cerintele temei.
    
    -> In clasa User am adaugat 2 campuri, numberOfRatings(numarul de rating-uri pe care le-a 	oferit utilizatorul), si ratedTitles, o lista cu titlurile videoclipurilor carora 		utilizatorul le-a acordat un rating.
    
    -> Datele sunt mutate din obiectele regasite in input in obiectele din "data" la inceputul 	metodei action. Sunt create liste noi, stocate in data, umplute de noile obiecte de tip 	User, Actor, Movie, Serial. Obiectele de tip Action nu au fost mutate deoarece nu am 	avut nevoie sa adaug ceva la Clasa ActionInputData, acestea puteau fi folosite direct.
    
Rezolvarea cerintelor:

    -> Problema incepe prin iterarea prin lista de actiuni regasit in obiectul input. Pentru 	fiecare comanda se preiau parametrii folositori tipului de comanda, se apeleaza metoda 	ce rezolva cerinta ceruta si se returneaza rezultatul, urmand, asa cum am precizat mai 	sus, sa fie creat un obiect JSON cu mesajul corespunzator rezultatului actiunii.
    
    -> Actiunile de tip "command" sunt realizate prin metode in clasele User, Movie si serial. 	Pentru view si favorite, se gaseste obiectul User corespunzator username-ului dat in 		comanda, si se apeleaza metoda direct pe acel obiect. Pentru add_rating se procedeaza 	la fel cu obiectul Movie sau Serial corespunzator, si din nou cu User, pentru a-i mari 	numarul de rating-uri si a adauga titltul in lista de videoclipuri care au primit 		rating(de la acel utilizator).
    
    -> Restul actiunilor sunt implementate in clasa Data, deoarece a fost nevoie de toate 		obiecte de tip User, Movie, Serial sau Actor.
    
    -> Toate actiunile de tip "Query" au la baza un map(HashMap sau TreeMap, depinzand de al 	doilea criteriu de stocare; daca aceste este alfabetic, TreeMap este folosit, daca este 		ordinea din baza de date, atunci HashMap este folosit). Map-urile au fost completate 	cu numele utilizatorului, al actorului sau al videoclipului insotit de valorea	 	corespunzatoare(in functie de tipul de query). Map-ul a fost sortat, in continuare, 		dupa 	valorea folosind streams si collector. Acesta a fost cel mai bun mod pe care 	l-am gasit pentru a sorta un map, ocupand doar 4 randuri si o singura insiruire de 		metode.
    
    -> La query-urile unde am filtre de an si de gen, am implementat metoda astfel incat sa 	functioneaza pentru mai mult de un singur filtru de gen(abia spre finalul temei am 		observat ca in teste se da maxim un filtru de gen). 
    
    -> Actiunile de tip "recommendation" sunt implementate similar cu cele de tip "query". 		Standard este singurul care a fost implementat fara un map, deoarece a trebuit sa 		parcurg videoclipurile in ordinea din baza de date si sa-l returnez pe primul nevazut.
    	Restul au fost implementate cu un map(cu rating, favorite, etc.). Actiunea popular a 	folosit un map diferit de restul metodelor, si anume un map intre genurile prezente in 		baza de date si numarul de vizualizari ale tuturor videoclipurilor care aparting de 	acel gen.
    	
    -> Verificarea tipului de rezultat si afisarea mesajului corespunzator fiecarei actiuni 	sunt efectuate in main, unde se creeaza toate obiectele JSON ce sunt adaugate in 		arrayResult.
    
Lucruri de imbunatatit:

    -> Pentru a accesa mai rapid obiectele de un anumit tip, in anumite situatii, as fi putut 	face map-uri intre obiecte de un anume tip si numele acestora. Aceasta idee mi-a venit 	pe la finalul temei, asa ca acest lucru il voi implementa in viitor(sau la teme si 		proiecte viitoare).
    
    -> Bucati de cod duplicat in multe metode. Am vrut sa construiesc metodele astfel incat sa 	fie implementarea unei actiuni de la capat la coada, codul fiin deja destul de        		complicat.
    
    -> Folosire switch in loc de multe if-uri in main.
    
    -> Aceste lucruri mentionate mai sus nu le-am mai realizat deoarece tema era deja gata, si 	era destul de complexa si mi-ar fi luat mult timp sa fac aceste schimbari.
    
    
Mentiuni:

    -> Metoda action din main are mai mult de 150 de linii, insa aceasta reprezinta scheletul 	temei si nu  am stiu cum sa o fac sa se incadreze in aceasta limita.     	
    
