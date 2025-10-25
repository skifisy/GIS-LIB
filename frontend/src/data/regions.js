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
              { name: '东大街道', code: 'dj', lon: 104.084, lat: 30.662 }
            ]
          },
          {
            name: '青羊区',
            code: 'QINGYANG',
            streets: [
              { name: '文殊院街道', code: 'ws', lon: 104.061, lat: 30.664 },
              { name: '草市街道', code: 'cs', lon: 104.055, lat: 30.665 }
            ]
          },
          {
            name: '金牛区',
            code: 'JINNIU',
            streets: [
              { name: '抚琴街道', code: 'fq', lon: 104.034, lat: 30.692 },
              { name: '西华街道', code: 'xh', lon: 104.043, lat: 30.679 }
            ]
          },
          {
            name: '武侯区',
            code: 'WU HOU',
            streets: [
              { name: '玉林街道', code: 'yl', lon: 104.056, lat: 30.650 },
              { name: '三官堂街道', code: 'sgt', lon: 104.060, lat: 30.640 }
            ]
          },
          {
            name: '成华区',
            code: 'CHENGHUA',
            streets: [
              { name: '双庆街道', code: 'sq', lon: 104.070, lat: 30.669 },
              { name: '建设路街道', code: 'jsl', lon: 104.099, lat: 30.673 }
            ]
          },
          {
            name: '高新区',
            code: 'GAOXIN',
            streets: [
              { name: '天府大道', code: 'tfd', lon: 104.070, lat: 30.600 },
              { name: '软件园街道', code: 'rjy', lon: 104.067, lat: 30.573 }
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
          }
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
          }
        ]
      }
    ]
  }
]
