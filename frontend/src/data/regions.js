// Expanded hierarchical region data with example coordinates (lon, lat)
// Structure: provinces -> cities -> districts -> streets
// NOTE: This is still sample/demo data. Replace with authoritative administrative data
// or integrate a geocoding/address API for production usage.
export default [
  {
    name: '四川省',
    code: 'SC',
    cities: [
      {
        name: '成都市',
        code: 'CD',
        districts: [
          {
            name: '锦江区',
            code: 'JINJIANG',
            streets: [
              { name: '春熙路街道', code: 'cxr', lon: 104.082, lat: 30.657 },
              { name: '东大街道', code: 'dj', lon: 104.084, lat: 30.662 },
              { name: '红星路街道', code: 'hx', lon: 104.078, lat: 30.656 }
            ]
          },
          {
            name: '青羊区',
            code: 'QINGYANG',
            streets: [
              { name: '文殊院街道', code: 'ws', lon: 104.061, lat: 30.664 },
              { name: '草市街道', code: 'cs', lon: 104.055, lat: 30.665 },
              { name: '琴台街道', code: 'qt', lon: 104.059, lat: 30.661 }
            ]
          },
          {
            name: '金牛区',
            code: 'JINNIU',
            streets: [
              { name: '抚琴街道', code: 'fq', lon: 104.034, lat: 30.692 },
              { name: '西华街道', code: 'xh', lon: 104.043, lat: 30.679 },
              { name: '茶店子街道', code: 'cdz', lon: 104.035, lat: 30.674 }
            ]
          },
          {
            name: '武侯区',
            code: 'WU HOU',
            streets: [
              { name: '玉林街道', code: 'yl', lon: 104.056, lat: 30.650 },
              { name: '三官堂街道', code: 'sgt', lon: 104.060, lat: 30.640 },
              { name: '浆洗街道', code: 'jx', lon: 104.052, lat: 30.646 },
              { name: '望江路', code: 'wj', lon: 104.057, lat: 30.646 }
            ]
          },
          {
            name: '成华区',
            code: 'CHENGHUA',
            streets: [
              { name: '双庆街道', code: 'sq', lon: 104.070, lat: 30.669 },
              { name: '建设路街道', code: 'jsl', lon: 104.099, lat: 30.673 },
              { name: '建设北路', code: 'jsbl', lon: 104.099, lat: 30.676 },
              { name: '东风街道', code: 'df', lon: 104.085, lat: 30.663 },
              { name: '二仙桥东三路', code: 'exqds3', lon: 104.103, lat: 30.678 }
            ]
          },
          {
            name: '高新区',
            code: 'GAOXIN',
            streets: [
              { name: '天府大道', code: 'tfd', lon: 104.070, lat: 30.600 },
              { name: '软件园街道', code: 'rjy', lon: 104.067, lat: 30.573 },
              { name: '华阳街道', code: 'hy', lon: 104.116, lat: 30.548 }
            ]
          },
          {
            name: '龙泉驿区',
            code: 'LONGQUAN',
            streets: [
              { name: '东山街道', code: 'ds', lon: 104.292, lat: 30.606 },
              { name: '抚琴街道', code: 'fq2', lon: 104.259, lat: 30.579 }
            ]
          },
          {
            name: '双流区',
            code: 'SHUANGLIU',
            streets: [
              { name: '空港街道', code: 'kg', lon: 103.962, lat: 30.572 },
              { name: '新川街道', code: 'xc', lon: 103.954, lat: 30.575 }
            ]
          },
          {
            name: '郫都区',
            code: 'PIDU',
            streets: [
              { name: '安德街道', code: 'ad', lon: 103.886, lat: 30.814 },
              { name: '犀浦街道', code: 'xp', lon: 103.889, lat: 30.757 }
              , { name: '合作街道', code: 'hz', lon: 103.873, lat: 30.782 }
              , { name: '犀安路', code: 'xa', lon: 103.885, lat: 30.771 }
            ]
          },
          {
            name: '温江区',
            code: 'WENJIANG',
            streets: [
              { name: '永宁街道', code: 'yn', lon: 103.842, lat: 30.700 },
              { name: '教场街道', code: 'jc', lon: 103.857, lat: 30.712 }
            ]
          },
          {
            name: '彭州市',
            code: 'PENGZHOU',
            streets: [
              { name: '天彭街道', code: 'tp', lon: 103.965, lat: 30.993 },
              { name: '石桥街道', code: 'sq', lon: 103.986, lat: 31.000 }
            ]
          },
          {
            name: '绵阳市',
            code: 'MY',
            districts: [
              {
                name: '涪城区',
                code: 'FUCHENG',
                streets: [
                  { name: '涪城街道A', code: 'fc_a', lon: 104.686, lat: 31.468 },
                  { name: '涪城街道B', code: 'fc_b', lon: 104.705, lat: 31.466 }
                ]
              },
              {
                name: '游仙区',
                code: 'YOUXIAN',
                streets: [
                  { name: '彭州路街道', code: 'pz', lon: 104.737, lat: 31.468 },
                  { name: '绵阳火车站周边', code: 'station', lon: 104.733, lat: 31.456 }
                ]
              },
              {
                name: '安州区',
                code: 'ANZHOU',
                streets: [
                  { name: '河西街道', code: 'hx', lon: 104.668, lat: 31.462 },
                  { name: '古城街道', code: 'gc', lon: 104.673, lat: 31.473 }
                ]
              }
            ]
          }
        ]
      }

    ]
  }
]
