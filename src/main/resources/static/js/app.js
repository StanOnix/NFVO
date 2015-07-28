
angular.module('app', ['ngRoute', 'ngSanitize', 'ui.bootstrap', 'ngCookies'])
        .config(function($routeProvider)
        {

            $routeProvider.
                    when('/', {
                        templateUrl: 'pages/contents.html',
                        controller: 'IndexCtrl'
                    }).
                    when('/services', {
                        templateUrl: 'pages/services/services.html',
                        controller: 'ServiceCtrl'
                    }).
                    when('/services/:serviceid', {
                        templateUrl: 'pages/services/serviceinfo.html',
                        controller: 'ServiceCtrl'
                    }).
                    when('/templates', {
                        templateUrl: 'pages/templates.html',
                        controller: 'TopologyCtrl'
                    }).
                    when('/templates/:templateid', {
                        templateUrl: 'pages/templateinfo.html',
                        controller: 'TopologyCtrl'
                    }).
                    when('/topologies', {
                        templateUrl: 'pages/topologies/topologies.html',
                        controller: 'TopologyCtrl'
                    }).
                    when('/topologies/:topologyid', {
                        templateUrl: 'pages/topologies/topologyinfo.html',
                        controller: 'TopologyCtrl'
                    }).
                    when('/topologies/:topologyid/graph', {
                        templateUrl: 'pages/topologies/graph.html',
                        controller: 'TopologyCtrl'
                    }).
                    when('/topologies/:topologyid/containers/:containerId', {
                        templateUrl: 'pages/topologies/unitsinfo.html',
                        controller: 'TopologyCtrl'
                    }).
                    when('/topologies/:topologyid/services/:serviceId', {
                        templateUrl: 'pages/topologies/serviceinstance.html',
                        controller: 'TopologyCtrl'
                    }).
                    when('/topologies/:topologyid/containers/:containerId/units/', {
                        templateUrl: 'pages/topologies/unitsinfo.html',
                        controller: 'TopologyCtrl'
                    }).
                    when('/infrastructures', {
                        templateUrl: 'pages/infrastructures/infrastructures.html',
                        controller: 'InfrastructureCtrl'
                    }).
                    when('/deployed/:topologyid', {
                        templateUrl: 'pages/infrastructures/deployed.html',
                        controller: 'InfrastructureCtrl'
                    }).
                    when('/infrastructures/:infrastructureid', {
                        templateUrl: 'pages/infrastructures/infrastructureinfo.html',
                        controller: 'InfrastructureCtrl'
                    }).
                    when('/switches', {
                        templateUrl: 'pages/switches/switches.html',
                        controller: 'SwitchCtrl'
                    }).
                    when('/switches/:switchid', {
                        templateUrl: 'pages/switches/switchinfo.html',
                        controller: 'SwitchCtrl'
                    }).
                    when('/flow/:flowid', {
                        templateUrl: 'pages/switches/flow.html',
                        controller: 'SwitchCtrl'
                    }).
                    when('/datacenters', {
                        templateUrl: 'pages/datacenters/datacenters.html',
                        controller: 'DataCenterCtrl'
                    }).
                    when('/datacenters/:dataCenterId', {
                        templateUrl: 'pages/datacenters/datacenterinfo.html',
                        controller: 'DataCenterCtrl'
                    }).
                    when('/controllers/:controllerid', {
                        templateUrl: 'pages/controllers/controllerinfo.html',
                        controller: 'ControllerCtrl'
                    }).
                    when('/controllers', {
                        templateUrl: 'pages/controllers/controllers.html',
                        controller: 'ControllerCtrl'
                    }).
                    when('/chains', {
                        templateUrl: 'pages/chains/chains.html',
                        controller: 'ChainsCtrl'
                    }).
                    when('/chains/:chainid', {
                        templateUrl: 'pages/chains/chaininfo.html',
                        controller: 'ChainsCtrl'
                    }).
                    when('/dragdrop', {
                        templateUrl: 'pages/tabset.html',
                        controller: 'DragDropCtrl'
                    }).
                    otherwise({
//                        redirectTo: '/'
                    });

        });



/*
 * MenuCtrl
 *
 * shows the modal for changing the Settings
 */

angular.module('app').controller('MenuCtrl', function($scope, http) {
    $scope.config = {};
  
//    http.syncGet('/api/rest/admin/v2/configs/').then(function(data)
//    {
//        $scope.config = data;
////        console.log($scope.config.parameters);
//
//    });


    $scope.saveSetting = function(config) {
        console.log(config);
        $('.modal').modal('hide');
        $('#modalSend').modal('show');

        http.post('/api/rest/admin/v2/configs/', config)
                .success(function(response) {
                    $('.modal').modal('hide');
                    alert('Configurations Updated! ' + response);

                })
                .error(function(response, status) {
                    $('.modal').modal('hide');
                    alert('ERROR: <strong>HTTP</strong> status:' + status + ' response <strong>response:</strong>' + response);
                });
    };

//    window.onresize = function() {
//        window.location.reload();
//    };




});