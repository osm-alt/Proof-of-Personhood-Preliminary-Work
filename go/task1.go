package main

import (
	"flag"
	"fmt"
	"log"
	"net/http"
	"strconv"

	"github.com/gorilla/websocket"
)

var addr = flag.String("addr", "localhost:11337", "http service address")

var upgrader = websocket.Upgrader{
	ReadBufferSize:  1024,
	WriteBufferSize: 1024,
	CheckOrigin: func(r *http.Request) bool {
		return true
	},
}

func handler(w http.ResponseWriter, r *http.Request) {
	conn, err := upgrader.Upgrade(w, r, nil)
	if err != nil {
		log.Println(err)
		return
	}
	//... Use conn to send and receive messages.
	count := 0

	for {

		messageType, recv, err := conn.ReadMessage()
		if err != nil {
			log.Println(err)
			return
		}

		recv_str := string(recv)

		log.Println("Received: " + recv_str)

		// Convert string to int
		num, err := strconv.Atoi(recv_str)
		if err != nil {
			fmt.Println(err)
			return
		}

		count = count + num

		send := strconv.Itoa(count)

		send_bytes := []byte(send)

		if err := conn.WriteMessage(messageType, send_bytes); err != nil {
			log.Println(err)
			return
		}

		log.Println("Sent: " + send)
	}
}

func main() {
	flag.Parse()
	log.SetFlags(0)
	http.HandleFunc("/", handler)
	log.Fatal(http.ListenAndServe(*addr, nil))
}
