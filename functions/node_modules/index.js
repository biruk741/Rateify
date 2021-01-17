const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);

// // Create and Deploy Your First Cloud Functions
// https://firebase.google.com/docs/functions/write-firebase-functions

// exports.helloWorld = functions.https.onRequest((request, response) => {
//  response.send("Hello from Firebase!");
// });
exports.countlikes = functions.database.ref('/uploads/$postid/rates').onWrite(event => {
  return event.data.ref.parent.child('rateCount').set(event.data.numChildren());
});
