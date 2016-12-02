/**
 * Created by AnshumanTripathi on 11/29/16.
 */
var mongoose = require("mongoose");
var schema = mongoose.Schema;

var user = new schema({
    "email": {type: String, required: true, unique: true},
    "first_name": {type: String},
    "last_name": {type: String},
    "home_address": {type: String},
    "screen_name": {type: String},
    "isOfficial": {type: Boolean, required: true},
    "settings": {
        "email_confirm": {type: Boolean},
        "email_notify": {type: Boolean},
        "anonymous": {type: Boolean}
    }
}, {
    collection: "user"
});


var User = mongoose.model('User', user);

user.pre('save', function (next) {
    if (this.screen_name == null) {
        this.screen_name = this.email;
    }
    next();
});

module.exports = User;