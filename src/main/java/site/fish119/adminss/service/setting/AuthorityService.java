package site.fish119.adminss.service.setting;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.fish119.adminss.domain.sys.Authority;
import site.fish119.adminss.repository.sys.SysAuthorityRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class AuthorityService {
    @Autowired
    public AuthorityService(SysAuthorityRepository authorityRepository) {
        this.authorityRepository = authorityRepository;
    }

    private final SysAuthorityRepository authorityRepository;

    public List<Authority> findAllAuthority() {
        return authorityRepository.findByParentIsNullOrderBySortAsc();
    }

    @Transactional
    public void saveAuthority(Long parentId, Authority authority) {
        LoggerFactory.getLogger(this.getClass()).info("authority:" + authority.getUrl());
        Authority parentMenu;
        if (authority.getId() != null) {
            Authority dbAuth = authorityRepository.findOne(authority.getId());
            dbAuth.setDescription(authority.getDescription());
            dbAuth.setName(authority.getName());
            dbAuth.setSort(authority.getSort());
            dbAuth.setUrl(authority.getUrl());
            dbAuth.setMethod(authority.getMethod());
            if (parentId != null) {
                parentMenu = authorityRepository.findOne(parentId);
                if (dbAuth.getParent() != null) {
                    Authority oldParent = authorityRepository.findOne(dbAuth.getParent().getId());
                    oldParent.getChildren().remove(dbAuth);
                    authorityRepository.save(oldParent);
                }
                dbAuth.setParent(parentMenu);
                parentMenu.getChildren().add(dbAuth);
                authorityRepository.save(parentMenu);
            } else {
                if (dbAuth.getParent() != null) {
                    Authority oldParent = authorityRepository.findOne(dbAuth.getParent().getId());
                    oldParent.getChildren().remove(authorityRepository.findOne(authority.getId()));
                    authorityRepository.save(oldParent);
                }
                dbAuth.setParent(null);

                LoggerFactory.getLogger(this.getClass()).info("dbAuth:" + dbAuth.getUrl());
                authorityRepository.save(dbAuth);
            }
        } else {
            if (parentId != null) {
                parentMenu = authorityRepository.findOne(parentId);
                if (parentMenu.getChildren().isEmpty()) {
                    Set<Authority> set = new HashSet<>();
                    set.add(authority);
                    parentMenu.setChildren(set);
                } else {
                    parentMenu.getChildren().add(authority);
                }
                authority.setParent(parentMenu);
                authorityRepository.save(parentMenu);
            } else {
                authorityRepository.save(authority);
            }
        }
    }

    @Transactional()
    public void delAuthority(Long id) {
        authorityRepository.delete(id);
    }

    @Transactional()
    public void delSubAuthority(Long parentId, Long id) {
//        Authority parentAuthority = authorityRepository.findOne(parentId);
//        Authority authority = authorityRepository.findOne(id);
//        authority.setParent(null);
//        parentAuthority.getChildren().remove(authority);
        authorityRepository.delete(id);
    }
}
