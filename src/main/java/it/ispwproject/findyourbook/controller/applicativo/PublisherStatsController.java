package it.ispwproject.findyourbook.controller.applicativo;

import it.ispwproject.findyourbook.bean.PublisherStatsBean;
import it.ispwproject.findyourbook.dao.DAOFactory;
import it.ispwproject.findyourbook.dao.PublisherDAO;
import it.ispwproject.findyourbook.exception.DAOException;
import it.ispwproject.findyourbook.model.PublisherStats;

public class PublisherStatsController {

    public PublisherStatsBean getStatistics(String publisherUsername) throws DAOException {

        PublisherDAO dao = DAOFactory.getPublisherDAO();

        PublisherStats model = dao.getPublisherStatistics(publisherUsername);

        return new PublisherStatsBean(
                model.getTotalBooksPublished(),
                model.getTotalCopiesSold(),
                model.getTopSellingBooks(),
                model.getSalesByGenre()
        );
    }
}