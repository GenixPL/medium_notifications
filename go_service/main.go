package main

import (
	"context"
	"fmt"
	"io"
	"net/http"

	"firebase.google.com/go"
	"firebase.google.com/go/messaging"
	"google.golang.org/api/option"
)

func main() {
	tokens := []string{}

	http.HandleFunc("/token", func(writer http.ResponseWriter, request *http.Request) {
		bytedata, err := io.ReadAll(request.Body)
		if err != nil {
			fmt.Println("token, read error:", err)
			return
		}

		newToken := string(bytedata)

		tokens = append(tokens, newToken)
	})

	http.HandleFunc("/poke", func(writer http.ResponseWriter, request *http.Request) {
		opt := option.WithCredentialsFile("firebase_key.json")
		config := &firebase.Config{
			ProjectID: "medium-notifications-ae40e",
		}
		firebaseApp, err := firebase.NewApp(context.Background(), config, opt)
		if err != nil {
			fmt.Println("poke, get app error:", err)
			return
		}

		messagingClient, err := firebaseApp.Messaging(context.Background())
		if err != nil {
			fmt.Println("poke, get messaging error:", err)
			return
		}

		for _, deviceToken := range tokens {
			_, err := messagingClient.Send(
				context.Background(),
				&messaging.Message{
					Token: deviceToken,
					Notification: &messaging.Notification{
						Title: "TITLE",
						Body:  "BODY",
					},
				},
			)
			if err != nil {
				fmt.Println("poke, send error:", err)
			}
		}
	})

	http.ListenAndServe(":8080", nil)
}
