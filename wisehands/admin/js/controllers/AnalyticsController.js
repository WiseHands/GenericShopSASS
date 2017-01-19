angular.module('WiseHands')
    .controller('AnalyticsController', ['$scope', '$http', 'sideNavInit', 'signout',
        function ($scope, $http, sideNavInit, signout) {
            $scope.loading = true;

            var token = localStorage.getItem('X-AUTH-TOKEN');
            var userId = localStorage.getItem('X-AUTH-USER-ID');

            $scope.getMainAnalyticsData = function (days) {
                $scope.loading = true;
                $scope.days = days;
                $http({
                    method: 'GET',
                    url: '/analytics' + days,
                    headers: {
                        'X-AUTH-TOKEN': token,
                        'X-AUTH-USER-ID': userId
                    }
                })
                    .then(function successCallback(response) {
                        $scope.analytics = response.data;

                        if(!$scope.analytics.totalToday){
                            $scope.analytics.totalToday = 0;
                        }

                        var labels = [];
                        var data = [];
                        for(var i=0; i<$scope.analytics.chartData.length; i++) {
                            var item = $scope.analytics.chartData[i];
                            labels.push(item.day);
                            data.push(item.total);
                        }

                        $scope.labels = labels;
                        $scope.series = ['Total'];
                        $scope.data = [
                            data
                        ];
                        // $scope.onClick = function (points, evt) {
                        //     console.log(points, evt);
                        // };
                        $scope.datasetOverride = [{ yAxisID: 'y-axis-1' }, { yAxisID: 'y-axis-2' }];
                        $scope.options = {
                            scales: {
                                yAxes: [
                                    {
                                        id: 'y-axis-1',
                                        type: 'linear',
                                        display: true,
                                        position: 'left'
                                    },
                                    {
                                        id: 'y-axis-2',
                                        type: 'linear',
                                        display: true,
                                        position: 'right'
                                    }
                                ]
                            }
                        };
                        $scope.loading = false;
                    }, function errorCallback(response) {
                        if (response.data === 'Invalid X-AUTH-TOKEN') {
                            signout.signOut();
                        }
                        $scope.status = 'Щось пішло не так...';
                    });

            };
                $scope.loading = true;
                $http({
                    method: 'GET',
                    url: '/orders',
                    headers: {
                        'X-AUTH-TOKEN': token,
                        'X-AUTH-USER-ID': userId
                    }
                })
                    .then(function successCallback(response) {
                        $scope.orders = response.data;
                        $scope.ordersAdresses = [];
                        $scope.orders.forEach (function(order){
                            if (order.destinationLat) {
                                var lat = parseFloat(order.destinationLat);
                                var lng = parseFloat(order.destinationLng);
                                var latLng = [];
                                latLng.push(lat);
                                latLng.push(lng);
                                $scope.ordersAdresses.push(latLng);
                            }
                        });
                        initialize($scope.ordersAdresses);
                        $scope.loading = false;
                    }, function errorCallback(response) {
                        if (response.data === 'Invalid X-AUTH-TOKEN') {
                            signout.signOut();
                        }
                        $scope.status = 'Щось пішло не так...';
                        $scope.loading = false;
                    });

            $scope.getMainAnalyticsData('');

            sideNavInit.sideNav();

        }]);

function initialize(latLng) {
    var markers = latLng;
    var map;
    var bounds = new google.maps.LatLngBounds();
    var mapOptions = {
        mapTypeId: 'roadmap'
    };

    map = new google.maps.Map(document.getElementById("map-container"), mapOptions);
    map.setTilt(45);

    var infoWindow = new google.maps.InfoWindow(), marker, i;

    for( i = 0; i < markers.length; i++ ) {
        var position = new google.maps.LatLng(markers[i][0], markers[i][1]);
        bounds.extend(position);
        marker = new google.maps.Marker({
            position: position,
            map: map
        });

        map.fitBounds(bounds);
        map.panToBounds(bounds);
    }

}



