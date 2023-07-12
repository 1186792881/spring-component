local zset_key = KEYS[1]
local min_score = ARGV[1]
local max_score = ARGV[2]
local offset = ARGV[3]
local limit = ARGV[4]
local status, type = next(redis.call('TYPE', zset_key))
if status ~= nil and status == 'ok' then
    if type == 'zset' then
        local list = redis.call('ZREVRANGEBYSCORE', zset_key, max_score, min_score, 'LIMIT', offset, limit)
        if list ~= nil and #list > 0 then
            redis.call('ZREM', zset_key, unpack(list))
            return list
        end
    end
end
return nil

