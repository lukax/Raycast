var mongoose     = require('mongoose');
var Schema       = mongoose.Schema;

var MessageSchema   = new Schema({
    author: { type: String, ref: 'Users' },
	message: String,
	time: Number,
	loc: { type: { type: String } , coordinates: [Number]  },
	ip: String
});

MessageSchema.index({ loc: '2dsphere' });

MessageSchema.static('findByAuthor', function (author, callback) {
	return this.find({'author': author }, null, {sort: {time: -1}}).populate('author').exec(callback);
});

MessageSchema.static('findByRadius', function (radius, latitude, longitude, skip, limit, time, callback) {
    var lat = Number(latitude) || null;
    var lon = Number(longitude) || null;
    var r = (radius / 3959000) || null;

    skip = Number(skip) || 0;
    limit = Number(limit) || 100;
    time = Number(time) || Date.now();

    if((lat == null) || (lon == null) || (r == null)){
        return null;
    }

    return this.find(
        {"loc":{"$geoWithin":{"$centerSphere":[[ lon , lat ], r]}}},
        null,
        {sort: {time: -1}, skip: Number(skip), limit: Number(limit)}
    )
    .where('time').lt(time)
    .populate('author')
    .exec(callback);
});

module.exports = mongoose.model('Messages', MessageSchema);
