import request from '../utils/request';



export const fetchData = query => {
    return request({
        url: './table.json',
        method: 'get',
        params: query
    });
};

export const postData = data => {
    return request({
        
        url: 'http://116.62.155.169:8080/resolve',
        method: 'post',
        data ,
    });
};

export const getDownload = filename => {
    return request({
        
        url: 'http://116.62.155.169:8080/download',
        method: 'get',
        params: filename,
        responseType: 'blob',
    });
};
