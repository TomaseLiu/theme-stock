ps -ef | grep java | grep theme-index | grep 1901 | awk '{print $2}' | xargs kill -9