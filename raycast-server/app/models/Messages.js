var mongoose     = require('mongoose');
var Schema       = mongoose.Schema;

var MessageSchema   = new Schema({
	author: String,
	message: String,
	time: Number,
	loc: { type: { type: String } , coordinates: [Number]  },
	ip: String
});

MessageSchema.index({ loc: '2dsphere' });

MessageSchema.static('findByAuthor', function (author, callback) {
	return this.find({ author: author }, callback);
});

MessageSchema.static('findByRadius', function (radius, latitude, longitude, skip, limit, callback) {
	return this.find(
		{"loc":{"$geoWithin":{"$centerSphere":[[ Number(longitude) , Number(latitude) ], radius/3959000]}}},
		null,
		{sort: {time: -1}, skip: Number(skip), limit: Number(limit)},
		callback
	).limit(Number(limit));
});

module.exports = mongoose.model('Messages', MessageSchema);
