package main

import (
	"context"
	"io"
	"net/http"

	"firebase.google.com/go"
	"firebase.google.com/go/messaging"
	"google.golang.org/api/option"
)

func main() {
	tokens := []string{}

	http.HandleFunc("/token", func(writer http.ResponseWriter, request *http.Request) {
		bytedata, readErr := io.ReadAll(request.Body)
		if readErr != nil {
			return
		}

		newToken := string(bytedata)

		tokens = append(tokens, newToken)
	})

	http.HandleFunc("/poke", func(writer http.ResponseWriter, request *http.Request) {
		opt := option.WithCredentialsFile("firebase_key.json")
		config := &firebase.Config{
			ProjectID: "medium-notifications-e0ad8",
		}
		firebaseApp, err := firebase.NewApp(context.Background(), config, opt)
		if err != nil {
			return
		}

		messagingClient, err := firebaseApp.Messaging(context.Background())
		if err != nil {
			return
		}

		for _, deviceToken := range tokens {
			messagingClient.Send(
				context.Background(),
				&messaging.Message{
					Token: deviceToken,
					Notification: &messaging.Notification{
						Title: "TITLE",
						Body:  "BODY",
					},
				},
			)
		}
	})

	http.ListenAndServe(":8080", nil)
}
